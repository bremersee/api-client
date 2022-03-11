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

import java.util.List;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The pageable request parameter resolver.
 *
 * @author Christian Bremer
 */
public class PageableRequestParameterResolver extends SortRequestParameterResolver {

  private String pageNumberRequestParamName = "page";

  private String pageSizeRequestParamName = "size";

  /**
   * With page number request param name.
   *
   * @param pageNumberRequestParamName the page number request param name
   * @return the pageable request parameter resolver
   */
  public PageableRequestParameterResolver withPageNumberRequestParamName(String pageNumberRequestParamName) {
    if (!isEmpty(pageNumberRequestParamName)) {
      this.pageNumberRequestParamName = pageNumberRequestParamName;
    }
    return this;
  }

  /**
   * With page size request param name.
   *
   * @param pageSizeRequestParamName the page size request param name
   * @return the pageable request parameter resolver
   */
  public PageableRequestParameterResolver withPageSizeRequestParamName(String pageSizeRequestParamName) {
    if (!isEmpty(pageSizeRequestParamName)) {
      this.pageSizeRequestParamName = pageSizeRequestParamName;
    }
    return this;
  }

  @Override
  public MultiValueMap<String, Object> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .filter(this)
        .map(InvocationParameter::getValue)
        .map(value -> getRequestParameters((Pageable) value))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::putAll,
            LinkedMultiValueMap::putAll);
  }

  @Override
  public boolean test(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Pageable
        && invocationParameter.hasNoneParameterAnnotation(Extensions.ILLEGAL_EXTENSIONS_ANNOTATIONS);
  }

  /**
   * Gets request parameters.
   *
   * @param pageable the pageable
   * @return the request parameters
   */
  protected MultiValueMap<String, Object> getRequestParameters(Pageable pageable) {
    MultiValueMap<String, Object> map = super.getRequestParameters(pageable.getSort());
    if (pageable.isPaged()) {
      map.put(pageNumberRequestParamName, List.of(pageable.getPageNumber()));
      map.put(pageSizeRequestParamName, List.of(pageable.getPageSize()));
    }
    return map;
  }
}
