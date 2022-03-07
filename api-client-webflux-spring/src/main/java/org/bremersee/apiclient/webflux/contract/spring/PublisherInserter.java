package org.bremersee.apiclient.webflux.contract.spring;

import java.lang.reflect.Method;
import java.util.Optional;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public class PublisherInserter extends SingleBodyInserter<InvocationParameter> {

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Publisher;
  }

  @Override
  protected InvocationParameter mapBody(InvocationParameter invocationParameter) {
    return invocationParameter;
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(
      InvocationParameter invocationParameter,
      RequestBodyUriSpec requestBodyUriSpec) {

    Method method = invocationParameter.getMethod();
    int index = invocationParameter.getIndex();
    //noinspection rawtypes
    return Optional.of(ResolvableType.forMethodParameter(method, index))
        .filter(ResolvableType::hasGenerics)
        .map(resolvableType -> resolvableType.resolveGeneric(0))
        .map(ParameterizedTypeReference::forType)
        .map(ref -> (RequestHeadersUriSpec) requestBodyUriSpec.body(invocationParameter.getValue(), ref))
        .orElse(requestBodyUriSpec);
  }

}
