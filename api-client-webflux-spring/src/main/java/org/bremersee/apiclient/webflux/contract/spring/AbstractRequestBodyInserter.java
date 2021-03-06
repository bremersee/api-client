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

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserter;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * The abstract request body inserter.
 *
 * @author Christian Bremer
 */
public abstract class AbstractRequestBodyInserter implements RequestBodyInserter {

  @Override
  public boolean canInsert(Invocation invocation) {
    return canInsert(findPossibleBodies(invocation));
  }

  /**
   * Can insert.
   *
   * @param possibleBodies the possible bodies
   * @return the boolean
   */
  protected boolean canInsert(List<InvocationParameter> possibleBodies) {
    return !possibleBodies.isEmpty();
  }

  /**
   * Find possible bodies list.
   *
   * @param invocation the invocation
   * @return the list
   */
  protected List<InvocationParameter> findPossibleBodies(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .filter(this::isPossibleBody)
        .sorted(new RequestBodyComparator())
        .collect(Collectors.toList());
  }

  /**
   * Is possible body boolean.
   *
   * @param invocationParameter the invocation parameter
   * @return the boolean
   */
  protected boolean isPossibleBody(InvocationParameter invocationParameter) {
    return nonNull(invocationParameter.getValue())
        && isPossibleBodyValue(invocationParameter)
        && hasMappingAnnotation(invocationParameter);
  }

  /**
   * Is possible body value boolean.
   *
   * @param invocationParameter the invocation parameter
   * @return the boolean
   */
  protected abstract boolean isPossibleBodyValue(InvocationParameter invocationParameter);

  /**
   * Has mapping annotation boolean.
   *
   * @param invocationParameter the invocation parameter
   * @return the boolean
   */
  protected boolean hasMappingAnnotation(InvocationParameter invocationParameter) {
    return invocationParameter.hasParameterAnnotation(RequestBody.class);
  }

}
