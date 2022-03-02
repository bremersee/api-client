/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.apiclient.webflux.spring;

import static java.util.Objects.nonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The api client.
 *
 * @author Christian Bremer
 */
public abstract class ApiClient {

  /**
   * Creates new proxy instance.
   *
   * @param <T> the api interface type
   * @param target the target
   * @param baseUrl the base url
   * @return the proxy
   */
  public abstract <T> T newInstance(Class<T> target, String baseUrl);

  /**
   * Default api client builder.
   *
   * @return the web client proxy builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * The default api client implementation.
   */
  public static class Default extends ApiClient {

    public <T> T newInstance(Class<T> target, String baseUrl) {
      return new Builder()
          .webClient(WebClient.builder()
              .baseUrl(baseUrl)
              .build())
          .build(target);
    }
  }

  /**
   * The builder.
   */
  public static class Builder {

    private final Map<MethodDescription, InvocationFunctions> methodFunctions = new HashMap<>();

    private InvocationFunctions commonFunctions;

    private WebClient webClient;

    /**
     * Sets the web client with the base url.
     *
     * @param webClient the web client
     * @return the builder
     */
    public Builder webClient(WebClient webClient) {
      this.webClient = webClient;
      return this;
    }

    /**
     * Sets special method functions.
     *
     * @param method the method
     * @param functions the functions
     * @return the builder
     */
    public Builder methodFunctions(
        Method method,
        InvocationFunctions functions) {

      if (nonNull(method) && nonNull(functions)) {
        methodFunctions.put(new MethodDescription(method), functions);
      }
      return this;
    }

    /**
     * Sets common functions.
     *
     * @param functions the functions
     * @return the builder
     */
    public Builder commonFunctions(InvocationFunctions functions) {
      this.commonFunctions = functions;
      return this;
    }

    /**
     * Builds proxy.
     *
     * @param <T> the type parameter
     * @param target the target
     * @return the t
     */
    public <T> T build(Class<T> target) {

      InvocationHandler handler = new WebClientInvocationHandler(
          Map.copyOf(methodFunctions),
          commonFunctions,
          nonNull(webClient) ? webClient : WebClient.builder().build(),
          target);
      //noinspection unchecked
      return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, handler);
    }

  }

}