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

import static java.util.Objects.nonNull;
import static org.springframework.core.GenericTypeResolver.resolveReturnTypeArgument;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import org.bremersee.apiclient.webflux.Invocation;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseFunction implements BiFunction<Invocation, ResponseSpec, Publisher<?>> {

  @Override
  public Publisher<?> apply(Invocation invocation, ResponseSpec responseSpec) {

    Method method = invocation.getMethod();
    Class<?> responseClass = method.getReturnType();
    if (Mono.class.isAssignableFrom(responseClass)) {
      Class<?> typeClass = resolveReturnTypeArgument(method, responseClass);
      return nonNull(typeClass)
          ? responseSpec.bodyToMono(typeClass)
          : responseSpec.bodyToMono(responseClass);
    }
    if (Flux.class.isAssignableFrom(responseClass)) {
      Class<?> typeClass = resolveReturnTypeArgument(method, responseClass);
      return nonNull(typeClass)
          ? responseSpec.bodyToFlux(typeClass)
          : responseSpec.bodyToFlux(responseClass);
    }
    if (Publisher.class.isAssignableFrom(responseClass)) {
      Class<?> typeClass = resolveReturnTypeArgument(method, responseClass);
      return nonNull(typeClass)
          ? responseSpec.bodyToFlux(typeClass)
          : responseSpec.bodyToFlux(responseClass);
    }
    throw new IllegalStateException(
        "Response class must be Mono, Flux or Publisher.");
  }
}
