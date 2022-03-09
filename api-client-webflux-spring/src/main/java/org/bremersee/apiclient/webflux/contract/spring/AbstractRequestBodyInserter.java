package org.bremersee.apiclient.webflux.contract.spring;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserter;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class AbstractRequestBodyInserter implements RequestBodyInserter {

  @Override
  public boolean canInsert(Invocation invocation) {
    return canInsert(findPossibleBodies(invocation));
  }

  protected boolean canInsert(List<InvocationParameter> possibleBodies) {
    return !possibleBodies.isEmpty();
  }

  protected List<InvocationParameter> findPossibleBodies(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .filter(this::isPossibleBody)
        .sorted(new RequestBodyComparator())
        .collect(Collectors.toList());
  }

  protected boolean isPossibleBody(InvocationParameter invocationParameter) {
    return nonNull(invocationParameter.getValue())
        && isPossibleBodyValue(invocationParameter)
        && hasMappingAnnotation(invocationParameter);
  }

  protected abstract boolean isPossibleBodyValue(InvocationParameter invocationParameter);

  protected boolean hasMappingAnnotation(InvocationParameter invocationParameter) {
    return invocationParameter.hasParameterAnnotation(RequestBody.class);
  }

}
