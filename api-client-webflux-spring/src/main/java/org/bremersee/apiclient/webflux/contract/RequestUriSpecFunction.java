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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface RequestUriSpecFunction
    extends BiFunction<Invocation, WebClient, RequestHeadersUriSpec<?>> {

  static ImmutableRequestUriSpecFunction.Builder builder() {
    return ImmutableRequestUriSpecFunction.builder();
  }

  @NotNull
  Function<Invocation, HttpRequestMethod> getHttpMethodResolver();

  @Override
  default RequestHeadersUriSpec<?> apply(Invocation invocation, WebClient webClient) {

    Assert.notNull(invocation, "Invocation must be present.");
    Assert.notNull(webClient, "Web client must be present.");
    return Optional.ofNullable(getHttpMethodResolver().apply(invocation))
        .map(httpRequestMethod -> httpRequestMethod.invoke(webClient))
        .orElseThrow(() -> new IllegalStateException(
            String.format("Cannot find request method on method '%s'.", invocation.getMethod().getName())));
  }

}
