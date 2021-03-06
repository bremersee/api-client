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

import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * The request headers resolver.
 *
 * @author Christian Bremer
 */
public class RequestHeadersResolver implements
    Function<Invocation, MultiValueMap<String, String>> {

  @Override
  public MultiValueMap<String, String> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .map(invocationParameter -> invocationParameter.toMultiValueMap(
            RequestHeader.class,
            RequestHeader::value,
            String::valueOf))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::addAll,
            LinkedMultiValueMap::addAll);
  }

}
