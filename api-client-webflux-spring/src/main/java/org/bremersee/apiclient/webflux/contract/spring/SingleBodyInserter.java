package org.bremersee.apiclient.webflux.contract.spring;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public abstract class SingleBodyInserter<T> extends AbstractRequestBodyInserter {

  protected boolean canInsert(List<InvocationParameter> possibleBodies) {
    if (possibleBodies.size() == 1) {
      return true;
    }
    return possibleBodies.stream()
        .filter(invocationParameter -> invocationParameter.hasParameterAnnotation(RequestBody.class))
        .count() == 1;
  }

  protected abstract boolean isPossibleBodyValue(InvocationParameter invocationParameter);

  @Override
  public RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec requestBodyUriSpec) {
    //noinspection unchecked,rawtypes
    return findBody(invocation)
        .map(this::mapBody)
        .map(body -> insert(body, requestBodyUriSpec))
        .orElse((RequestHeadersUriSpec) requestBodyUriSpec);
  }

  protected abstract RequestHeadersUriSpec<?> insert(T body, RequestBodyUriSpec requestBodyUriSpec);

  protected Optional<InvocationParameter> findBody(Invocation invocation) {
    return Optional.of(findPossibleBodies(invocation))
        .filter(this::canInsert)
        .stream()
        .flatMap(Collection::stream)
        .findFirst();
  }

  protected abstract T mapBody(InvocationParameter invocationParameter);

}
