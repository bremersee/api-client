package org.bremersee.apiclient.webflux.contract.spring;

import static java.util.Objects.nonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultipartDataInserter extends AbstractRequestBodyInserter {

  private ContentTypeResolver contentTypeResolver = new ContentTypeResolver();

  public MultipartDataInserter withContentTypeResolver(ContentTypeResolver contentTypeResolver) {
    if (nonNull(contentTypeResolver)) {
      this.contentTypeResolver = contentTypeResolver;
    }
    return this;
  }

  @Override
  public boolean canInsert(Invocation invocation) {
    return isMultipartFormData(invocation) && super.canInsert(invocation);
  }

  @Override
  protected boolean canInsert(List<InvocationParameter> possibleBodies) {
    return !possibleBodies.isEmpty();
  }

  protected boolean isMultipartFormData(Invocation invocation) {
    return contentTypeResolver.apply(invocation)
        .filter(contentType -> contentType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA))
        .isPresent();
  }

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return isMultiValueMap(invocationParameter) || isRequestPart(invocationParameter);
  }

  protected boolean hasMappingAnnotation(InvocationParameter invocationParameter) {
    return super.hasMappingAnnotation(invocationParameter)
        || invocationParameter.hasParameterAnnotation(RequestPart.class);
  }

  protected boolean isMultiValueMap(InvocationParameter invocationParameter) {
    Method method = invocationParameter.getMethod();
    int index = invocationParameter.getIndex();
    if (invocationParameter.getValue() instanceof MultiValueMap) {
      return Optional.of(ResolvableType.forMethodParameter(method, index))
          .filter(resolvableType -> resolvableType.getGenerics().length >= 2)
          .map(resolvableType -> {
            Class<?> r0 = resolvableType.resolveGeneric(0);
            Class<?> r1 = resolvableType.resolveGeneric(1);
            return nonNull(r0) && nonNull(r1)
                && String.class.isAssignableFrom(r0) && Part.class.isAssignableFrom(r1);
          })
          .isPresent();
    } else if (invocationParameter.getValue() instanceof Publisher) {
      return Optional.of(ResolvableType.forMethodParameter(method, index))
          .filter(ResolvableType::hasGenerics)
          .map(resolvableType -> resolvableType.getGeneric(0))
          .filter(resolvableType -> resolvableType.getGenerics().length >= 2)
          .map(resolvableType -> {
            Class<?> r0 = resolvableType.resolveGeneric(0);
            Class<?> r1 = resolvableType.resolveGeneric(1);
            return nonNull(r0) && nonNull(r1)
                && String.class.isAssignableFrom(r0) && Part.class.isAssignableFrom(r1);
          })
          .isPresent();
    }
    return false;
  }

  protected boolean isRequestPart(InvocationParameter invocationParameter) {
    return invocationParameter.hasParameterAnnotation(RequestPart.class)
        && (isPart(invocationParameter));
  }

  private boolean isPart(InvocationParameter invocationParameter) {
    if (invocationParameter.getValue() instanceof Part) {
      return true;
    } else if (invocationParameter.getValue() instanceof Publisher) {
      Method method = invocationParameter.getMethod();
      int index = invocationParameter.getIndex();
      return Optional.of(ResolvableType.forMethodParameter(method, index))
          .filter(ResolvableType::hasGenerics)
          .map(resolvableType -> resolvableType.resolveGeneric(0))
          .filter(Part.class::isAssignableFrom)
          .isPresent();
    }
    return false;
  }

  @Override
  public RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec requestBodyUriSpec) {
    // TODO in beiden fällen ale parts extrahieren und eine map draus machen
    List<InvocationParameter> possibleBodies = findPossibleBodies(invocation);
    List<Publisher<Part>> parts = possibleBodies.stream()
        .filter(this::isRequestPart)
        .map(invocationParameter -> toPublisher(invocationParameter.getValue()))
        .collect(Collectors.toList());
    if (!parts.isEmpty()) {
      Mono<MultiValueMap<String, HttpEntity<?>>> map = Flux.concat(parts)
          .map(part -> {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part(part.name(), part);
            return builder.build();
          })
          .flatMap(m -> Flux.fromStream(m.entrySet().stream()))
          .collectMap(Entry::getKey, Entry::getValue, LinkedMultiValueMap::new)
          .map(m -> (MultiValueMap<String, HttpEntity<?>>) m);
      //noinspection rawtypes
      return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters
          .fromPublisher(map, new MultiValueMapTypeReference()));
    } else {
      Publisher<MultiValueMap<String, Part>> publisher;
      if (possibleBodies.get(0).getValue() instanceof Publisher) {
        //noinspection unchecked
        publisher = (Publisher<MultiValueMap<String, Part>>) possibleBodies.get(0).getValue();
      } else {
        //noinspection unchecked
        publisher = Mono.just((MultiValueMap<String, Part>) possibleBodies.get(0).getValue());
      }
      Mono<MultiValueMap<String, HttpEntity<?>>> map = Flux.from(publisher)
          .map(m -> {
            MultiValueMap<String, HttpEntity<?>> vm = new LinkedMultiValueMap<>();
            for (Map.Entry<String, List<Part>> entry : m.entrySet()) {
              for (Part part : entry.getValue()) {
                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part(entry.getKey(), part);
                vm.addAll(builder.build());
              }
            }
            return vm;
          })
          .flatMap(m -> Flux.fromStream(m.entrySet().stream()))
          .collectMap(Entry::getKey, Entry::getValue, LinkedMultiValueMap::new)
          .map(m -> (MultiValueMap<String, HttpEntity<?>>) m);
      //noinspection rawtypes
      return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters
          .fromPublisher(map, new MultiValueMapTypeReference()));
    }
  }

  private Publisher<Part> toPublisher(Object value) {
    Publisher<Part> part;
    if (value instanceof Part) {
      part = Mono.just((Part) value);
    } else {
      //noinspection unchecked
      part = (Publisher<Part>) value;
    }
    return part;
  }

  private static class MultiValueMapTypeReference
      extends ParameterizedTypeReference<MultiValueMap<String, HttpEntity<?>>> {

  }
}
