package org.bremersee.apiclient.webflux.contract.spring;

import static java.util.Objects.nonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
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
    return invocationParameter.getValue() instanceof MultiValueMap
        || isRequestPart(invocationParameter);
  }

  protected boolean isRequestPart(InvocationParameter invocationParameter) {
    return invocationParameter.hasParameterAnnotation(RequestPart.class)
        && (isPart(invocationParameter));
  }

  private boolean isPart(InvocationParameter invocationParameter) {
    if (invocationParameter.getValue() instanceof Part) {
      return true;
    }
    if (invocationParameter.getValue() instanceof Publisher) {
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
    List<Publisher<Part>> parts = possibleBodies.stream()
        .filter(this::isRequestPart)
        .map(invocationParameter -> toPublisher(invocationParameter.getValue()))
        .collect(Collectors.toList());
    if (!parts.isEmpty()) {
      Flux<Part> fluxParts = Flux.concat(parts);
      //noinspection rawtypes
      return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters.fromPublisher(fluxParts, Part.class));
    } else {
      //noinspection unchecked
      MultiValueMap<String, ?> body = (MultiValueMap<String, ?>) possibleBodies.get(0).getValue();
      //noinspection rawtypes
      return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters.fromMultipartData(body));
    }
  }

  private Publisher<Part> toPublisher(Object value) {
    if (value instanceof Part) {
      return Mono.just((Part) value);
    }
    //noinspection unchecked
    return (Publisher<Part>) value;
  }

}
