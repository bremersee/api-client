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

package org.bremersee.apiclient.webflux;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface ReactiveContract {

  static ImmutableReactiveContract.Builder builder() {
    return ImmutableReactiveContract.builder();
  }

  @NotNull
  BiConsumer<Invocation, MultiValueMap<String, String>> getCookiesConsumer();

  @NotNull
  BiConsumer<Invocation, HttpHeaders> getHeadersConsumer();

  @NotNull
  BiFunction<Invocation, UriBuilder, URI> getRequestUriFunction();

  @NotNull
  BiFunction<Invocation, WebClient, RequestHeadersUriSpec<?>> getRequestUriSpecFunction();

  @NotNull
  BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> getRequestBodyInserterFunction();

  @NotNull
  BiFunction<Invocation, ResponseSpec, Publisher<?>> getResponseFunction();

}
