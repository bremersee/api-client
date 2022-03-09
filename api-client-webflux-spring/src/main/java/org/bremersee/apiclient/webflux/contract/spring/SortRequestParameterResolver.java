package org.bremersee.apiclient.webflux.contract.spring;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SortRequestParameterResolver implements
    Function<Invocation, MultiValueMap<String, Object>>,
    Predicate<InvocationParameter> {

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
  public MultiValueMap<String, Object> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .filter(this)
        .map(InvocationParameter::getValue)
        .map(value -> getRequestParameters((Sort) value))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::putAll,
            LinkedMultiValueMap::putAll);
  }

  @Override
  public boolean test(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Sort
        && invocationParameter.hasNoneParameterAnnotation(Extensions.ILLEGAL_EXTENSIONS_ANNOTATIONS);
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
