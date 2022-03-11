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

import java.util.List;
import java.util.function.BiFunction;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface RequestBodyInserterRegistry extends
    BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> {

  static ImmutableRequestBodyInserterRegistry.Builder builder() {
    return ImmutableRequestBodyInserterRegistry.builder();
  }

  @NotEmpty
  List<RequestBodyInserter> getRequestBodyInserters();

  @Override
  default RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec requestBodyUriSpec) {
    //noinspection unchecked,rawtypes
    return getRequestBodyInserters().stream()
        .filter(inserter -> inserter.canInsert(invocation))
        .findFirst()
        .map(inserter -> inserter.apply(invocation, requestBodyUriSpec))
        .orElse((RequestHeadersUriSpec) requestBodyUriSpec);
  }
}
