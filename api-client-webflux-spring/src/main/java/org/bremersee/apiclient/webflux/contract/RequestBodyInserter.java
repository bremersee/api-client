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

package org.bremersee.apiclient.webflux.contract;

import java.util.function.BiFunction;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The request body inserter.
 *
 * @author Christian Bremer
 */
public interface RequestBodyInserter extends
    BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> {

  /**
   * Can insert boolean.
   *
   * @param invocation the invocation
   * @return the boolean
   */
  boolean canInsert(Invocation invocation);

}
