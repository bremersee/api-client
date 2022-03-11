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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public enum HttpRequestMethod {
  GET(WebClient::get),
  HEAD(WebClient::head),
  POST(WebClient::post),
  PUT(WebClient::put),
  PATCH(WebClient::patch),
  DELETE(WebClient::delete),
  OPTIONS(WebClient::options);
  //TRACE();

  private final Function<WebClient, RequestHeadersUriSpec<?>> uriSpecFunction;

  HttpRequestMethod(Function<WebClient, RequestHeadersUriSpec<?>> uriSpecFunction) {
    this.uriSpecFunction = uriSpecFunction;
  }

  public RequestHeadersUriSpec<?> invoke(WebClient webClient) {
    return uriSpecFunction.apply(webClient);
  }

  public static Optional<HttpRequestMethod> resolve(String method) {
    return Arrays.stream(HttpRequestMethod.values())
        .filter(httpRequestMethod -> httpRequestMethod.name().equalsIgnoreCase(method))
        .findFirst();
  }
}
