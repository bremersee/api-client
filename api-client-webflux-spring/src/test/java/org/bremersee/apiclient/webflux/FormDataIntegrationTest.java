package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.FormDataController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/**
 * The api client integration test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"security.basic.enabled=false"})
@Slf4j
class FormDataIntegrationTest {

  @LocalServerPort
  int port;

  FormDataController apiClient;

  String baseUrl() {
    return "http://localhost:" + port;
  }

  @BeforeEach
  void init() {
    apiClient = new ReactiveApiClient(WebClient.builder(), new ReactiveSpringContract())
        .newInstance(FormDataController.class, baseUrl());
  }

  @Test
  void postFormData() {
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("k0", "v0");
    data.add("k1", "v1");

    LinkedHashMap<String, Object> expected = new LinkedHashMap<>();
    expected.put("x-ok-flag", "a-flag");
    expected.put("last", "a-value");
    expected.putAll(data);

    StepVerifier.create(apiClient.postFormData("a-flag", "a-value", data))
        .assertNext(response -> assertThat(response).isEqualTo(expected))
        .expectNextCount(0)
        .verifyComplete();
  }

}