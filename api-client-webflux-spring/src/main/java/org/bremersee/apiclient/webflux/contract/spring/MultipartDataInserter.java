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

  protected boolean isMultipartFormData(Invocation invocation) {
    return contentTypeResolver.apply(invocation)
        .filter(contentType -> contentType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA))
        .isPresent();
  }

  @Override
  protected boolean hasMappingAnnotation(InvocationParameter invocationParameter) {
    return super.hasMappingAnnotation(invocationParameter)
        || invocationParameter.hasParameterAnnotation(RequestPart.class);
  }

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return isMultiValueMap(invocationParameter) || isRequestPart(invocationParameter);
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
    List<InvocationParameter> possibleBodies = findPossibleBodies(invocation);
    List<Publisher<Part>> partPublishers = possibleBodies.stream()
        .filter(this::isRequestPart)
        .map(invocationParameter -> toPublisher(invocationParameter.getValue()))
        .collect(Collectors.toList());
    Mono<MultiValueMap<String, HttpEntity<?>>> httpEntityMap;
    if (!partPublishers.isEmpty()) {
      httpEntityMap = toHttpEntityMap(partPublishers);
    } else {
      Publisher<MultiValueMap<String, Part>> partMap = findRequestBody(possibleBodies);
      httpEntityMap = toHttpEntityMap(partMap);
    }
    //noinspection rawtypes
    return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters
        .fromPublisher(httpEntityMap, new MultiValueMapTypeReference()));
  }

  private Publisher<Part> toPublisher(Object value) {
    Publisher<Part> partPublisher;
    if (value instanceof Part) {
      partPublisher = Mono.just((Part) value);
    } else {
      //noinspection unchecked
      partPublisher = (Publisher<Part>) value;
    }
    return partPublisher;
  }

  private Mono<MultiValueMap<String, HttpEntity<?>>> toHttpEntityMap(List<Publisher<Part>> partPublishers) {
    return Flux.concat(partPublishers)
        .map(part -> {
          MultipartBodyBuilder builder = new MultipartBodyBuilder();
          builder.part(part.name(), part);
          return builder.build();
        })
        .flatMap(httpEntityMap -> Flux.fromStream(httpEntityMap.entrySet().stream()))
        .collectMap(Entry::getKey, Entry::getValue, LinkedMultiValueMap::new)
        .map(httpEntityMap -> (MultiValueMap<String, HttpEntity<?>>) httpEntityMap);
  }

  @SuppressWarnings("unchecked")
  private Publisher<MultiValueMap<String, Part>> findRequestBody(List<InvocationParameter> possibleBodies) {
    return possibleBodies.stream()
        .findFirst()
        .map(InvocationParameter::getValue)
        .map(value -> {
          if (value instanceof Publisher) {
            return (Publisher<MultiValueMap<String, Part>>) value;
          } else {
            MultiValueMap<String, Part> partMap = (MultiValueMap<String, Part>) value;
            return Mono.just(partMap);
          }
        })
        .orElseGet(Mono::empty);
  }

  private Mono<MultiValueMap<String, HttpEntity<?>>> toHttpEntityMap(
      Publisher<MultiValueMap<String, Part>> partMapPublisher) {

    return Flux.from(partMapPublisher)
        .map(partMap -> {
          MultiValueMap<String, HttpEntity<?>> httpEntityMap = new LinkedMultiValueMap<>();
          for (Map.Entry<String, List<Part>> partMapEntry : partMap.entrySet()) {
            for (Part part : partMapEntry.getValue()) {
              MultipartBodyBuilder builder = new MultipartBodyBuilder();
              builder.part(partMapEntry.getKey(), part);
              httpEntityMap.addAll(builder.build());
            }
          }
          return httpEntityMap;
        })
        .flatMap(httpEntityMap -> Flux.fromStream(httpEntityMap.entrySet().stream()))
        .collectMap(Entry::getKey, Entry::getValue, LinkedMultiValueMap::new)
        .map(httpEntityMap -> (MultiValueMap<String, HttpEntity<?>>) httpEntityMap);
  }

  private static class MultiValueMapTypeReference
      extends ParameterizedTypeReference<MultiValueMap<String, HttpEntity<?>>> {

  }
}
