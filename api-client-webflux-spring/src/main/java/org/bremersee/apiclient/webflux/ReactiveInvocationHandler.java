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

package org.bremersee.apiclient.webflux;

import static java.util.Objects.isNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

/**
 * The api client invocation handler.
 *
 * @author Christian Bremer
 */
@Slf4j
public class ReactiveInvocationHandler implements InvocationHandler {

  private final Class<?> targetClass;

  private final WebClient webClient;

  private final ReactiveContract contract;

  private final ReactiveErrorHandler errorHandler;

  public ReactiveInvocationHandler(
      Class<?> targetClass,
      WebClient webClient,
      ReactiveContract contract,
      ReactiveErrorHandler errorHandler) {

    Assert.notNull(targetClass, "Target class must be present.");
    Assert.notNull(webClient, "Web client must be present.");
    this.targetClass = targetClass;
    this.webClient = webClient;
    this.contract = isNull(contract)
        ? new ReactiveSpringContract()
        : contract;
    this.errorHandler = isNull(errorHandler)
        ? ReactiveErrorHandler.builder().build()
        : errorHandler;
  }

  @SuppressWarnings("SuspiciousInvocationHandlerImplementation")
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    if (ReflectionUtils.isObjectMethod(method)) {
      if (ReflectionUtils.isEqualsMethod(method)) {
        return this.equals(args[0]);
      } else if (ReflectionUtils.isHashCodeMethod(method)) {
        return this.hashCode();
      } else if (ReflectionUtils.isToStringMethod(method)) {
        return String.format("Reactive api client of %s", targetClass.getName());
      } else {
        return ReflectionUtils.invokeMethod(method, this, args);
      }
    }
    Invocation invocation = new Invocation(targetClass, method, args);
    RequestHeadersUriSpec<?> uriSpec = contract
        .getRequestUriSpecFunction()
        .apply(invocation, webClient);
    uriSpec = (RequestHeadersUriSpec<?>) uriSpec.uri(uriBuilder -> contract
            .getRequestUriFunction()
            .apply(invocation, uriBuilder))
        .headers(httpHeaders -> contract.getHeadersConsumer().accept(invocation, httpHeaders))
        .cookies(cookies -> contract.getCookiesConsumer().accept(invocation, cookies));
    if (uriSpec instanceof RequestBodyUriSpec) {
      uriSpec = contract
          .getRequestBodyInserterFunction()
          .apply(invocation, (RequestBodyUriSpec) uriSpec);
    }
    ResponseSpec responseSpec = uriSpec.retrieve();
    responseSpec = responseSpec
        .onStatus(errorHandler.getErrorPredicate(), errorHandler.getErrorFunction());
    return contract
        .getResponseFunction()
        .apply(invocation, responseSpec);
  }
}
