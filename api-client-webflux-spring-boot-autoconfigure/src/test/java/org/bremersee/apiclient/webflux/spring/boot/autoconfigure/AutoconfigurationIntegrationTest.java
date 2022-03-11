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

package org.bremersee.apiclient.webflux.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.ReactiveApiClient;
import org.bremersee.apiclient.webflux.ReactiveContract;
import org.bremersee.apiclient.webflux.ReactiveErrorHandler;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserter;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserterRegistry;
import org.bremersee.apiclient.webflux.contract.spring.ContentTypeResolver;
import org.bremersee.apiclient.webflux.contract.spring.QueryParametersResolver;
import org.bremersee.apiclient.webflux.contract.spring.multipart.PartBuilder;
import org.bremersee.apiclient.webflux.spring.boot.autoconfigure.app.ControllerApi;
import org.bremersee.apiclient.webflux.spring.boot.autoconfigure.app.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"security.basic.enabled=false"})
@ExtendWith(SoftAssertionsExtension.class)
@Slf4j
public class AutoconfigurationIntegrationTest {

  @LocalServerPort
  int port;

  @Autowired
  ReactiveErrorHandler reactiveErrorHandler;

  @Autowired
  ContentTypeResolver contentTypeResolver;

  @Autowired
  List<QueryParametersResolver> queryParametersResolvers;

  @Autowired
  List<RequestBodyInserter> requestBodyInserters;

  @Autowired
  RequestBodyInserterRegistry requestBodyInserterRegistry;

  @Autowired
  ReactiveContract reactiveSpringContract;

  @Autowired
  ReactiveApiClient reactiveApiClient;

  ControllerApi controllerApi;

  private String baseUrl() {
    return "http://localhost:" + port;
  }

  @BeforeEach
  void init() {
    controllerApi = reactiveApiClient.newInstance(ControllerApi.class, baseUrl());
  }

  @Test
  void context(SoftAssertions softly) {
    softly.assertThat(reactiveErrorHandler).isNotNull();
    softly.assertThat(contentTypeResolver).isNotNull();
    softly.assertThat(queryParametersResolvers).hasSize(3);
    softly.assertThat(requestBodyInserters).hasSize(6);
    softly.assertThat(requestBodyInserterRegistry).isNotNull();
    softly.assertThat(reactiveSpringContract).isNotNull();
  }

  @Test
  void postData() {
    StepVerifier
        .create(controllerApi.postData(DataBufferUtils
            .read(new ClassPathResource("ok.txt"), new DefaultDataBufferFactory(), 256)))
        .assertNext(response -> assertThat(response).contains("OK"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postParts() {
    FormFieldPart part = new PartBuilder()
        .part("string", "OK")
        .build();
    StepVerifier
        .create(controllerApi.postParts(part))
        .assertNext(response -> assertThat(response).contains("OK"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postPublisher() {
    StepVerifier
        .create(controllerApi.postPublisher(Mono.just("OK")))
        .assertNext(response -> assertThat(response).contains("OK"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postResource() {
    StepVerifier
        .create(controllerApi.postResource(new ClassPathResource("ok.txt")))
        .assertNext(response -> assertThat(response).contains("OK"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void putStringValue() {
    Map<String, Object> expected = Map.of(
        "name", "anna",
        "payload", "OK"
    );
    StepVerifier
        .create(controllerApi.putStringValue("anna", "OK"))
        .assertNext(response -> assertThat(response).containsExactlyInAnyOrderEntriesOf(expected))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void getPage() {
    PageApi api = reactiveApiClient.newInstance(PageApi.class, baseUrl());
    Map<String, Object> expected = Map.of(
        "page", 8,
        "size", 15,
        "sort", List.of("lastname", "firstname")
    );
    StepVerifier
        .create(api.getPage(PageRequest.of(8, 15, Sort.by("lastname", "firstname"))))
        .assertNext(response -> assertThat(response).containsExactlyInAnyOrderEntriesOf(expected))
        .expectNextCount(0)
        .verifyComplete();
  }

  interface PageApi {

    @GetMapping(path = "/api/page", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<Map<String, Object>> getPage(Pageable pageable);
  }

}
