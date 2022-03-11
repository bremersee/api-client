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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The headers consumer test.
 *
 * @author Christian Bremer
 */
class HeadersConsumerTest {

  /**
   * Accept.
   */
  @Test
  void accept() {
    //noinspection unchecked
    Function<Invocation, Optional<MediaType>> contentTypeResolver = mock(Function.class);
    when(contentTypeResolver.apply(any()))
        .thenReturn(Optional.of(MediaType.APPLICATION_JSON));

    //noinspection unchecked
    Function<Invocation, MediaType> acceptResolver = mock(Function.class);
    when(acceptResolver.apply(any()))
        .thenReturn(MediaType.ALL);

    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, String>> headersResolver = mock(Function.class);
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Authorization", "b");
    when(headersResolver.apply(any()))
        .thenReturn(headers);

    HttpHeaders expected = new HttpHeaders();
    expected.setContentType(MediaType.APPLICATION_JSON);
    expected.setAccept(List.of(MediaType.ALL));
    expected.add("Authorization", "b");

    HeadersConsumer target = HeadersConsumer.builder()
        .contentTypeResolver(contentTypeResolver)
        .acceptResolver(acceptResolver)
        .headersResolver(headersResolver)
        .build();
    Invocation invocation = mock(Invocation.class);
    HttpHeaders actual = new HttpHeaders();
    target.accept(invocation, actual);
    assertThat(actual)
        .isEqualTo(expected);
  }
}