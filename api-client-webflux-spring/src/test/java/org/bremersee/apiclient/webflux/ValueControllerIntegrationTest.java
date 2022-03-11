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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.app.ValueController;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.test.StepVerifier;

/**
 * The value controller integration test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"security.basic.enabled=false"})
@Slf4j
class ValueControllerIntegrationTest {

  /**
   * The Port.
   */
  @LocalServerPort
  int port;

  /**
   * The Web client.
   */
  WebClient webClient;

  /**
   * The Api client.
   */
  ValueController apiClient;

  /**
   * Base url string.
   *
   * @return the string
   */
  String baseUrl() {
    return "http://localhost:" + port;
  }

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    apiClient = new ReactiveApiClient(WebClient.builder(), new ReactiveSpringContract())
        .newInstance(ValueController.class, baseUrl());
    webClient = WebClient.builder()
        .baseUrl(baseUrl())
        .build();
  }

  /**
   * Gets string value with web client.
   */
  @Test
  void getStringValueWithWebClient() {
    StepVerifier
        .create(webClient
            .get()
            .uri(UriBuilder::build)
            .retrieve()
            .bodyToMono(String.class))
        .assertNext(response -> assertEquals(ValueController.STRING_VALUE, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Gets string value.
   */
  @Test
  void getStringValue() {
    StepVerifier.create(apiClient.getStringValue())
        .assertNext(response -> assertEquals(ValueController.STRING_VALUE, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Gets json values.
   */
  @Test
  void getJsonValues() {
    StepVerifier.create(apiClient.getJsonValues())
        .assertNext(map0 -> assertThat(map0).isEqualTo(ValueController.JSON_VALUES.get(0)))
        .assertNext(map1 -> assertThat(map1).isEqualTo(ValueController.JSON_VALUES.get(1)))
        .assertNext(map2 -> assertThat(map2).isEqualTo(ValueController.JSON_VALUES.get(2)))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Put string value.
   */
  @Test
  void putStringValue() {
    StepVerifier.create(apiClient.putStringValue("value", "ok"))
        .assertNext(response -> assertThat(response).isEqualTo("value=ok"))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Patch string value.
   */
  @Test
  void patchStringValue() {
    StepVerifier.create(apiClient.patchStringValue("name", "suffix", "payload"))
        .expectNextCount(0)
        .verifyComplete();

    StepVerifier.create(apiClient.patchStringValue("name", "exception", "payload"))
        .expectError(RuntimeException.class)
        .verifyThenAssertThat();
  }

  /**
   * Post value.
   */
  @Test
  void postValue() {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("x-custom", "1234");
    Map<String, Object> requestParams = Map.of("r0", "v0");
    Map<String, Object> payload = Map.of("body", "hello");

    Map<String, Object> expected = new LinkedHashMap<>();
    expected.put("PathVariable", "path-to-test");
    expected.put("x-custom", "1234");
    expected.put("sweet", "and_tasty");
    expected.putAll(requestParams);
    expected.putAll(payload);

    StepVerifier.create(apiClient
            .postValue("path-to-test", headers, "and_tasty", requestParams, payload))
        .assertNext(response -> assertThat(response).isEqualTo(expected))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Delete value.
   */
  @Test
  void deleteValue() {
    StepVerifier.create(apiClient.deleteValue("value"))
        .assertNext(value -> assertThat(value).isTrue())
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Call object methods.
   */
  @Test
  void callObjectMethods() {
    ValueController proxy = apiClient;
    assertEquals(proxy.hashCode(), proxy.hashCode());
    assertNotEquals(proxy, null);
    assertNotNull(proxy.toString());
  }

}