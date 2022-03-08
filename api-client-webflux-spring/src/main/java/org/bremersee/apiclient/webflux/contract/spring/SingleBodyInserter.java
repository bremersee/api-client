package org.bremersee.apiclient.webflux.contract.spring;

import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public abstract class SingleBodyInserter<T> extends AbstractRequestBodyInserter {

  protected abstract boolean isPossibleBodyValue(InvocationParameter invocationParameter);

  @Override
  public RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec requestBodyUriSpec) {
    //noinspection unchecked,rawtypes
    return findPossibleBodies(invocation)
        .stream()
        .findFirst()
        .map(this::mapBody)
        .map(body -> insert(body, requestBodyUriSpec))
        .orElse((RequestHeadersUriSpec) requestBodyUriSpec);
  }

  protected abstract RequestHeadersUriSpec<?> insert(T body, RequestBodyUriSpec requestBodyUriSpec);

  protected abstract T mapBody(InvocationParameter invocationParameter);

}
