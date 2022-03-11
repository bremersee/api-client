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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The value inserter test.
 *
 * @author Christian Bremer
 */
class ValueInserterTest {

  private static final ValueInserter target = new ValueInserter();

  /**
   * Is possible body value.
   */
  @Test
  void isPossibleBodyValue() {
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn("123");
    assertThat(target.isPossibleBodyValue(invocationParameter))
        .isTrue();
  }

  /**
   * Map body.
   */
  @Test
  void mapBody() {
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn("123");
    assertThat(target.mapBody(invocationParameter))
        .isEqualTo("123");
  }

  /**
   * Insert.
   *
   * @throws Exception the exception
   */
  @Test
  void insert() throws Exception {
    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(requestBodyUriSpec.body(any(BodyInserter.class)))
        .thenReturn(expected);

    RequestHeadersUriSpec<?> actual = target.insert("123", requestBodyUriSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }
}