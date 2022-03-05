package org.bremersee.apiclient.webflux.function.resolver.spring;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.apiclient.webflux.function.resolver.AbstractInvocationParameterResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SortRequestParameterResolver
    extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>> {

  private String requestParamName = "sort";

  private String separatorValue = ",";

  private String descValue = "desc";

  public SortRequestParameterResolver withRequestParamName(String requestParamName) {
    if (!isEmpty(requestParamName)) {
      this.requestParamName = requestParamName;
    }
    return this;
  }

  public SortRequestParameterResolver withSeparatorValue(String separatorValue) {
    if (!isEmpty(separatorValue)) {
      this.separatorValue = separatorValue;
    }
    return this;
  }

  public SortRequestParameterResolver withDescValue(String descValue) {
    if (!isEmpty(descValue)) {
      this.descValue = descValue;
    }
    return this;
  }

  @Override
  public boolean canResolve(InvocationParameter invocationParameter) {
    return (invocationParameter.getValue() instanceof Sort)
        && invocationParameter.hasNoneParameterAnnotation(Extensions.ILLEGAL_EXTENSIONS_ANNOTATIONS);
  }

  @Override
  public MultiValueMap<String, Object> resolve(InvocationParameter invocationParameter) {
    return Optional.ofNullable(invocationParameter.getValue())
        .filter(value -> value instanceof Sort)
        .map(value -> (Sort) value)
        .map(this::getRequestParameters)
        .orElseGet(LinkedMultiValueMap::new);
  }

  protected MultiValueMap<String, Object> getRequestParameters(Sort sort) {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    if (sort.isSorted()) {
      map.put(requestParamName, sort.stream().map(this::getRequestParamValue).collect(Collectors.toList()));
    }
    return map;
  }

  protected String getRequestParamValue(Order order) {
    StringBuilder sb = new StringBuilder(order.getProperty());
    if (order.isDescending()) {
      sb.append(separatorValue).append(descValue);
    }
    return sb.toString();
  }
}
