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

import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The cookies consumer test.
 */
class CookiesConsumerTest {

  /**
   * Accept.
   */
  @Test
  void accept() {
    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, String>> cookiesResolver = mock(Function.class);
    MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
    expected.add("a", "b");
    when(cookiesResolver.apply(any()))
        .thenReturn(expected);
    CookiesConsumer target = CookiesConsumer.builder()
        .cookiesResolver(cookiesResolver)
        .build();
    Invocation invocation = mock(Invocation.class);
    MultiValueMap<String, String> actual = new LinkedMultiValueMap<>();
    target.accept(invocation, actual);
    assertThat(actual)
        .isEqualTo(expected);
  }
}