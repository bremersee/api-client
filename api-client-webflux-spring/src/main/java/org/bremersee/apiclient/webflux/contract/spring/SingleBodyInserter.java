package org.bremersee.apiclient.webflux.contract.spring;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public abstract class SingleBodyInserter<T> implements RequestBodyInserter {
  // Zwischenschritt: Liste sammeln possible

  @Override
  public boolean canInsert(Invocation invocation) {
    return findBody(invocation).isPresent();
  }

  private boolean canInsert(List<InvocationParameter> possibleBodies) {
    if (possibleBodies.size() == 1) {
      return true;
    }
    return possibleBodies.stream()
        .filter(invocationParameter -> invocationParameter.hasParameterAnnotation(RequestBody.class))
        .count() == 1;
  }

  // immer parameter zurück geben
  protected Optional<T> findBody(Invocation invocation) {
    List<InvocationParameter> possibleBodies = invocation.toMethodParameterStream()
        .filter(this::isPossibleBody)
        .sorted(new SingleBodyComparator())
        .collect(Collectors.toList());
    //noinspection unchecked
    return Optional.of(possibleBodies)
        .filter(this::canInsert)
        .stream()
        .flatMap(Collection::stream)
        .findFirst()
        .map(this::mapBody);
  }

  // TODO abstract
  public T mapBody(InvocationParameter invocationParameter) {
    return (T) invocationParameter.getValue();
  }

  protected boolean isPossibleBody(InvocationParameter invocationParameter) {
    return isPossibleBodyValue(invocationParameter)
        && hasNoneMappingAnnotation(invocationParameter);
  }

  protected abstract boolean isPossibleBodyValue(InvocationParameter invocationParameter);

  protected boolean hasNoneMappingAnnotation(InvocationParameter invocationParameter) {
    //noinspection unchecked
    return invocationParameter.hasNoneParameterAnnotation(
        CookieValue.class,
        MatrixVariable.class,
        ModelAttribute.class,
        PathVariable.class,
        RequestAttribute.class,
        RequestHeader.class,
        RequestParam.class,
        RequestPart.class, // bei sammeln der Liste beachten -> als methode zurück geben, Liste!
        SessionAttribute.class
    );
  }

  @Override
  public RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec requestBodyUriSpec) {
    //noinspection unchecked,rawtypes
    return findBody(invocation)
        .map(body -> insert(body, requestBodyUriSpec))
        .orElse((RequestHeadersUriSpec) requestBodyUriSpec);
  }

  protected abstract RequestHeadersUriSpec<?> insert(T body, RequestBodyUriSpec requestBodyUriSpec);

}
