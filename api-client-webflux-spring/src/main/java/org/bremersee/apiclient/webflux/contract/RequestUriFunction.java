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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;

/**
 * The request uri function.
 *
 * @author Christian Bremer
 */
@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface RequestUriFunction extends BiFunction<Invocation, UriBuilder, URI> {

  /**
   * Builder.
   *
   * @return the request uri function builder
   */
  static ImmutableRequestUriFunction.Builder builder() {
    return ImmutableRequestUriFunction.builder();
  }

  /**
   * Gets request path resolver.
   *
   * @return the request path resolver
   */
  @NotNull
  Function<Invocation, String> getRequestPathResolver();

  /**
   * Gets path variables resolver.
   *
   * @return the path variables resolver
   */
  @NotNull
  Function<Invocation, Map<String, Object>> getPathVariablesResolver();

  /**
   * Gets request parameters resolvers.
   *
   * @return the request parameters resolvers
   */
  @NotEmpty
  List<Function<Invocation, MultiValueMap<String, Object>>> getRequestParametersResolvers();

  @Override
  default URI apply(Invocation invocation, UriBuilder uriBuilder) {
    UriBuilder builder = uriBuilder.path(getRequestPathResolver().apply(invocation));
    MultiValueMap<String, Object> requestParameters = getRequestParametersResolvers().stream()
        .map(resolver -> resolver.apply(invocation))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::putAll,
            LinkedMultiValueMap::putAll);
    for (Map.Entry<String, List<Object>> entry : requestParameters.entrySet()) {
      builder = builder.queryParam(entry.getKey(), entry.getValue());
    }
    return builder.build(getPathVariablesResolver().apply(invocation));
  }

}
