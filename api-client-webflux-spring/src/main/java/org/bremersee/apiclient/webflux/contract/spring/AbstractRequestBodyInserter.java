package org.bremersee.apiclient.webflux.contract.spring;

import java.util.List;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

public abstract class AbstractRequestBodyInserter implements RequestBodyInserter {

  @Override
  public boolean canInsert(Invocation invocation) {
    return canInsert(findPossibleBodies(invocation));
  }

  protected abstract boolean canInsert(List<InvocationParameter> possibleBodies);

  protected List<InvocationParameter> findPossibleBodies(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .filter(this::isPossibleBody)
        .sorted(new RequestBodyComparator())
        .collect(Collectors.toList());
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
        SessionAttribute.class
    );
  }

}
