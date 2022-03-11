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

/**
 * The http request method.
 */
public enum HttpRequestMethod {

  /**
   * Get http request method.
   */
  GET(WebClient::get),

  /**
   * Head http request method.
   */
  HEAD(WebClient::head),

  /**
   * Post http request method.
   */
  POST(WebClient::post),

  /**
   * Put http request method.
   */
  PUT(WebClient::put),

  /**
   * Patch http request method.
   */
  PATCH(WebClient::patch),

  /**
   * Delete http request method.
   */
  DELETE(WebClient::delete),

  /**
   * Options http request method.
   */
  OPTIONS(WebClient::options);

  private final Function<WebClient, RequestHeadersUriSpec<?>> uriSpecFunction;

  HttpRequestMethod(Function<WebClient, RequestHeadersUriSpec<?>> uriSpecFunction) {
    this.uriSpecFunction = uriSpecFunction;
  }

  /**
   * Invoke request headers uri spec.
   *
   * @param webClient the web client
   * @return the request headers uri spec
   */
  public RequestHeadersUriSpec<?> invoke(WebClient webClient) {
    return uriSpecFunction.apply(webClient);
  }

  /**
   * Resolve.
   *
   * @param method the method
   * @return the optional
   */
  public static Optional<HttpRequestMethod> resolve(String method) {
    return Arrays.stream(HttpRequestMethod.values())
        .filter(httpRequestMethod -> httpRequestMethod.name().equalsIgnoreCase(method))
        .findFirst();
  }
}
