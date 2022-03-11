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

package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The single body inserter test.
 *
 * @author Christian Bremer
 */
class SingleBodyInserterTest {

  /**
   * Apply.
   */
  @Test
  void apply() {
    //noinspection unchecked
    SingleBodyInserter<String> target = mock(SingleBodyInserter.class);
    when(target.apply(any(), any())).thenCallRealMethod();
    String value = "123";
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn(value);
    when(target.findPossibleBodies(any())).thenReturn(List.of(invocationParameter));
    when(target.mapBody(any())).thenReturn(value);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(target.insert(anyString(), any())).thenReturn(expected);
    RequestHeadersUriSpec<?> actual = target.apply(
        mock(Invocation.class),
        mock(RequestBodyUriSpec.class));
    assertThat(actual)
        .isEqualTo(expected);
  }
}