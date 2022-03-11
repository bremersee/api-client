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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.DataBufferController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/**
 * The data buffer controller integration test.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"security.basic.enabled=false"})
@Slf4j
class DataBufferControllerIntegrationTest {

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
  DataBufferController apiClient;

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
    apiClient = ReactiveApiClient.builder()
        .webClient(WebClient.builder()
            .baseUrl(baseUrl())
            .build())
        .contract(new ReactiveSpringContract())
        .errorHandler(ReactiveErrorHandler.builder().build())
        .build(DataBufferController.class);
    webClient = WebClient.builder()
        .baseUrl(baseUrl())
        .build();
  }

  /**
   * Post resource with web client.
   */
  @Test
  void postResourceWithWebClient() {
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/data")
            .contentType(MediaType.TEXT_PLAIN)
            .body(BodyInserters.fromDataBuffers(DataBufferUtils
                .read(new ClassPathResource("text.txt"), new DefaultDataBufferFactory(), 256)))
            .retrieve()
            .bodyToMono(String.class))
        .assertNext(response -> assertThat(response).isEqualTo("Hello world!"))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Post data.
   */
  @Test
  void postData() {
    StepVerifier
        .create(apiClient.postData(DataBufferUtils
            .read(new ClassPathResource("text.txt"), new DefaultDataBufferFactory(), 256)))
        .assertNext(response -> assertThat(response).isEqualTo("Hello world!"))
        .expectNextCount(0)
        .verifyComplete();
  }

}