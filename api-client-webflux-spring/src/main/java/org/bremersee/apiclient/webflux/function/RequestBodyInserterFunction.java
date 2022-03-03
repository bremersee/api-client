package org.bremersee.apiclient.webflux.function;

import static java.util.Objects.nonNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

@Builder(toBuilder = true)
public class RequestBodyInserterFunction
    implements BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> {

  @NonNull
  private Function<Invocation, Optional<InvocationParameter>> requestBodyResolver;

  @NonNull
  private Function<Invocation, Optional<MediaType>> contentTypeResolver;

  @Override
  public RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec uriSpec) {
    return requestBodyResolver.apply(invocation)
        .filter(invocationParameter -> nonNull(invocationParameter.getValue()))
        .map(invocationParameter -> {
          Object value = invocationParameter.getValue();
          if (isFormData(invocationParameter)) {
            //noinspection unchecked
            uriSpec.body(BodyInserters.fromFormData((MultiValueMap<String, String>) value));
          } else if (isMultipartFormData(invocationParameter)) {
            //noinspection unchecked
            uriSpec.body(BodyInserters.fromMultipartData((MultiValueMap<String, ?>) value));
          } else if (value instanceof Resource) {
            uriSpec.body(BodyInserters.fromResource((Resource) value));
          } else {
            uriSpec.body(BodyInserters.fromValue(value));
          }
          return uriSpec;
        })
        .orElse(uriSpec);
  }

  protected boolean isFormData(InvocationParameter invocationParameter) {
    return contentTypeResolver.apply(invocationParameter)
        .filter(contentType -> contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .filter(ct -> invocationParameter.getValue() instanceof MultiValueMap)
        .isPresent();
  }


  protected boolean isMultipartFormData(InvocationParameter invocationParameter) {
    return contentTypeResolver.apply(invocationParameter)
        .filter(contentType -> contentType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA))
        .filter(ct -> invocationParameter.getValue() instanceof MultiValueMap)
        .isPresent();
  }

}
