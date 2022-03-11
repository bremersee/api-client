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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The http request method.
 *
 * @author Christian Bremer
 */
public enum HttpRequestMethod {

  /**
   * Get http request method.
   */
  GET,

  /**
   * Head http request method.
   */
  HEAD,

  /**
   * Post http request method.
   */
  POST,

  /**
   * Put http request method.
   */
  PUT,

  /**
   * Patch http request method.
   */
  PATCH,

  /**
   * Delete http request method.
   */
  DELETE,

  /**
   * Options http request method.
   */
  OPTIONS;

  /**
   * Invoke request headers uri spec.
   *
   * @param webClient the web client
   * @return the request headers uri spec
   */
  public RequestHeadersUriSpec<?> invoke(WebClient webClient) {
    switch (this) {
      case GET:
        return webClient.get();
      case HEAD:
        return webClient.head();
      case POST:
        return webClient.post();
      case PUT:
        return webClient.put();
      case PATCH:
        return webClient.patch();
      case DELETE:
        return webClient.delete();
      case OPTIONS:
        return webClient.options();
      default:
        throw new IllegalStateException(String.format(
            "There is no action defined for http method  %s",
            this.name()));
    }
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
