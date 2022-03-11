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

import java.util.function.Function;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * The reactive error handler.
 *
 * @author Christian Bremer
 */
@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface ReactiveErrorHandler {

  /**
   * Builder.
   *
   * @return the immutable reactive error handler builder
   */
  static ImmutableReactiveErrorHandler.Builder builder() {
    return ImmutableReactiveErrorHandler.builder();
  }

  /**
   * Gets error predicate.
   *
   * @return the error predicate
   */
  @Value.Default
  @NotNull
  default Predicate<HttpStatus> getErrorPredicate() {
    return HttpStatus::isError;
  }

  /**
   * Gets error function.
   *
   * @return the error function
   */
  @Nullable
  Function<ClientResponse, Mono<? extends Throwable>> getErrorFunction();

}
