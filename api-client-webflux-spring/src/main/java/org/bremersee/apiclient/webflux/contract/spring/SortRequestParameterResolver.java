/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.apiclient.webflux.contract.spring;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The sort request parameter resolver.
 *
 * @author Christian Bremer
 */
public class SortRequestParameterResolver implements
    QueryParametersResolver,
    Predicate<InvocationParameter> {

  private String requestParamName = "sort";

  private String separatorValue = ",";

  private String descValue = "desc";

  /**
   * With request parameter name.
   *
   * @param requestParamName the request param name
   * @return the sort request parameter resolver
   */
  public SortRequestParameterResolver withRequestParamName(String requestParamName) {
    if (!isEmpty(requestParamName)) {
      this.requestParamName = requestParamName;
    }
    return this;
  }

  /**
   * With separator value.
   *
   * @param separatorValue the separator value
   * @return the sort request parameter resolver
   */
  public SortRequestParameterResolver withSeparatorValue(String separatorValue) {
    if (!isEmpty(separatorValue)) {
      this.separatorValue = separatorValue;
    }
    return this;
  }

  /**
   * With desc value.
   *
   * @param descValue the desc value
   * @return the sort request parameter resolver
   */
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

  /**
   * Gets request parameters.
   *
   * @param sort the sort
   * @return the request parameters
   */
  protected MultiValueMap<String, Object> getRequestParameters(Sort sort) {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    if (sort.isSorted()) {
      map.put(requestParamName, sort.stream().map(this::getRequestParamValue).collect(Collectors.toList()));
    }
    return map;
  }

  /**
   * Gets request param value.
   *
   * @param order the order
   * @return the request param value
   */
  protected String getRequestParamValue(Order order) {
    StringBuilder sb = new StringBuilder(order.getProperty());
    if (order.isDescending()) {
      sb.append(separatorValue).append(descValue);
    }
    return sb.toString();
  }

}
