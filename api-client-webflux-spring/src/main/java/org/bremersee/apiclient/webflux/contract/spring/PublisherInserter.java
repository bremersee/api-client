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

import java.lang.reflect.Method;
import java.util.Optional;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The publisher inserter.
 *
 * @author Christian Bremer
 */
public class PublisherInserter extends SingleBodyInserter<InvocationParameter> {

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Publisher;
  }

  @Override
  protected InvocationParameter mapBody(InvocationParameter invocationParameter) {
    return invocationParameter;
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(
      InvocationParameter invocationParameter,
      RequestBodyUriSpec requestBodyUriSpec) {

    Method method = invocationParameter.getMethod();
    int index = invocationParameter.getIndex();
    //noinspection rawtypes
    return Optional.of(ResolvableType.forMethodParameter(method, index))
        .filter(ResolvableType::hasGenerics)
        .map(resolvableType -> resolvableType.resolveGeneric(0))
        .map(ParameterizedTypeReference::forType)
        .map(ref -> (RequestHeadersUriSpec) requestBodyUriSpec
            .body(invocationParameter.getValue(), ref))
        .orElse(requestBodyUriSpec);
  }

}
