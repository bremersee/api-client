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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The http request method test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class HttpRequestMethodTest {

  /**
   * Invoke.
   */
  @Test
  void invoke() {
    WebClient webClient = mock(WebClient.class);
    HttpRequestMethod.HEAD.invoke(webClient);
    verify(webClient).head();

    reset(webClient);
    HttpRequestMethod.DELETE.invoke(webClient);
    verify(webClient).delete();

    reset(webClient);
    HttpRequestMethod.GET.invoke(webClient);
    verify(webClient).get();

    reset(webClient);
    HttpRequestMethod.OPTIONS.invoke(webClient);
    verify(webClient).options();

    reset(webClient);
    HttpRequestMethod.PATCH.invoke(webClient);
    verify(webClient).patch();

    reset(webClient);
    HttpRequestMethod.POST.invoke(webClient);
    verify(webClient).post();

    reset(webClient);
    HttpRequestMethod.PUT.invoke(webClient);
    verify(webClient).put();
  }

  /**
   * Resolve.
   *
   * @param softly the softly
   */
  @Test
  void resolve(SoftAssertions softly) {
    softly.assertThat(HttpRequestMethod.resolve("HEAD"))
        .hasValue(HttpRequestMethod.HEAD);
    softly.assertThat(HttpRequestMethod.resolve("DELETE"))
        .hasValue(HttpRequestMethod.DELETE);
    softly.assertThat(HttpRequestMethod.resolve("GET"))
        .hasValue(HttpRequestMethod.GET);
    softly.assertThat(HttpRequestMethod.resolve("OPTIONS"))
        .hasValue(HttpRequestMethod.OPTIONS);
    softly.assertThat(HttpRequestMethod.resolve("PATCH"))
        .hasValue(HttpRequestMethod.PATCH);
    softly.assertThat(HttpRequestMethod.resolve("POST"))
        .hasValue(HttpRequestMethod.POST);
    softly.assertThat(HttpRequestMethod.resolve("PUT"))
        .hasValue(HttpRequestMethod.PUT);
    softly.assertThat(HttpRequestMethod.resolve("TRACE"))
        .isEmpty();
  }
}