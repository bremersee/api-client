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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The request uri function test.
 */
class RequestUriFunctionTest {

  /**
   * Apply.
   */
  @Test
  void apply() {
    RequestUriFunction target = mock(RequestUriFunction.class);
    when(target.apply(any(), any())).thenCallRealMethod();

    //noinspection unchecked
    Function<Invocation, String> requestPathResolver = mock(Function.class);
    when(requestPathResolver.apply(any())).thenReturn("http://localhost:8080/api/books/{id}");
    when(target.getRequestPathResolver()).thenReturn(requestPathResolver);

    //noinspection unchecked
    Function<Invocation, Map<String, Object>> pathVariablesResolver = mock(Function.class);
    when(pathVariablesResolver.apply(any())).thenReturn(Map.of("id", "1234"));
    when(target.getPathVariablesResolver()).thenReturn(pathVariablesResolver);

    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, Object>> requestParametersResolver0 = mock(Function.class);
    MultiValueMap<String, Object> parameterMap0 = new LinkedMultiValueMap<>();
    parameterMap0.add("sort", "a");
    parameterMap0.add("sort", "b");
    parameterMap0.add("size", 25);
    when(requestParametersResolver0.apply(any())).thenReturn(parameterMap0);

    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, Object>> requestParametersResolver1 = mock(Function.class);
    MultiValueMap<String, Object> parameterMap1 = new LinkedMultiValueMap<>();
    parameterMap1.add("sort", "c");
    parameterMap1.add("sort", "d");
    parameterMap1.add("page", 10);
    when(requestParametersResolver1.apply(any())).thenReturn(parameterMap1);

    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, Object>> requestParametersResolver2 = mock(Function.class);
    MultiValueMap<String, Object> parameterMap2 = new LinkedMultiValueMap<>();
    parameterMap2.add("sort", "e");
    parameterMap2.add("foo", "bar");
    when(requestParametersResolver2.apply(any())).thenReturn(parameterMap2);

    List<Function<Invocation, MultiValueMap<String, Object>>> requestParametersResolvers = List.of(
        requestParametersResolver0,
        requestParametersResolver1,
        requestParametersResolver2
    );
    when(target.getRequestParametersResolvers()).thenReturn(requestParametersResolvers);

    UriBuilder uriBuilder = UriComponentsBuilder.fromUriString("");
    URI actual = target.apply(mock(Invocation.class), uriBuilder);

    // last wins
    assertThat(actual)
        .asString()
        .isEqualTo("http:/localhost:8080/api/books/1234?sort=e&size=25&page=10&foo=bar");
  }
}