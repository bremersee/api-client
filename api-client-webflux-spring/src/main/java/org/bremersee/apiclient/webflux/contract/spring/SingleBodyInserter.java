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

import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The single body inserter.
 *
 * @param <T> the type parameter
 */
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

  /**
   * Insert request headers uri spec.
   *
   * @param body the body
   * @param requestBodyUriSpec the request body uri spec
   * @return the request headers uri spec
   */
  protected abstract RequestHeadersUriSpec<?> insert(T body, RequestBodyUriSpec requestBodyUriSpec);

  /**
   * Map body.
   *
   * @param invocationParameter the invocation parameter
   * @return the t
   */
  protected abstract T mapBody(InvocationParameter invocationParameter);

}
