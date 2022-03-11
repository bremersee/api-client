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

import static java.util.Objects.isNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.bremersee.apiclient.ApiClient;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

public class ReactiveApiClient extends ApiClient {

  private final WebClient.Builder webClientBuilder;

  private final ReactiveContract contract;

  private final ReactiveErrorHandler errorHandler;

  public ReactiveApiClient(
      WebClient.Builder webClientBuilder,
      ReactiveContract contract) {

    this(webClientBuilder, contract, null);
  }

  public ReactiveApiClient(
      WebClient.Builder webClientBuilder,
      ReactiveContract contract,
      ReactiveErrorHandler errorHandler) {

    this.webClientBuilder = isNull(webClientBuilder) ? WebClient.builder() : webClientBuilder;
    this.contract = contract;
    this.errorHandler = errorHandler;
  }

  @Override
  public <T> T newInstance(Class<T> target, String baseUrl) {
    return new Builder()
        .webClient(webClientBuilder.baseUrl(baseUrl).build())
        .contract(contract)
        .errorHandler(errorHandler)
        .build(target);
  }

  /**
   * Default api client builder.
   *
   * @return the web client proxy builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * The builder.
   */
  public static class Builder {

    private WebClient webClient;

    private ReactiveContract contract;

    private ReactiveErrorHandler errorHandler;

    Builder() {
    }

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

    public Builder contract(ReactiveContract functionBundle) {
      this.contract = functionBundle;
      return this;
    }

    public Builder errorHandler(ReactiveErrorHandler errorFunctionBundle) {
      this.errorHandler = errorFunctionBundle;
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
      Assert.notNull(target, "Target must be present.");
      InvocationHandler handler = new ReactiveInvocationHandler(
          target,
          webClient,
          contract,
          errorHandler);
      //noinspection unchecked
      return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, handler);
    }

  }

}
