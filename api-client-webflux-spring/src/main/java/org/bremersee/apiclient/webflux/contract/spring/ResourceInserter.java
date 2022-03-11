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

import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The resource inserter.
 *
 * @author Christian Bremer
 */
public class ResourceInserter extends SingleBodyInserter<Resource> {

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Resource;
  }

  @Override
  protected Resource mapBody(InvocationParameter invocationParameter) {
    //noinspection
    return (Resource) invocationParameter.getValue();
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(
      Resource body,
      RequestBodyUriSpec requestBodyUriSpec) {

    //noinspection rawtypes
    return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters.fromResource(body));
  }

}
