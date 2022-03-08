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
import org.bremersee.exception.RestApiResponseException;
import org.bremersee.exception.webclient.DefaultWebClientErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
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
@AutoConfigureWebTestClient
@Slf4j
class ValueControllerIntegrationTest {

  @LocalServerPort
  int port;

  WebClient webClient;

  ValueController apiClient;

  String baseUrl() {
    return "http://localhost:" + port;
  }

  @BeforeEach
  void init() {
    apiClient = new ReactiveApiClient(
        WebClient.builder(),
        new ReactiveSpringContract(),
        ReactiveErrorHandler.builder()
            .errorFunction(new DefaultWebClientErrorDecoder())
            .build())
        .newInstance(ValueController.class, baseUrl());
    webClient = WebClient.builder()
        .baseUrl(baseUrl())
        .build();
  }

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

  @Test
  void getStringValue() {
    StepVerifier.create(apiClient.getStringValue())
        .assertNext(response -> assertEquals(ValueController.STRING_VALUE, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void getJsonValues() {
    StepVerifier.create(apiClient.getJsonValues())
        .assertNext(map0 -> assertThat(map0).isEqualTo(ValueController.JSON_VALUES.get(0)))
        .assertNext(map1 -> assertThat(map1).isEqualTo(ValueController.JSON_VALUES.get(1)))
        .assertNext(map2 -> assertThat(map2).isEqualTo(ValueController.JSON_VALUES.get(2)))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void putStringValue() {
    StepVerifier.create(apiClient.putStringValue("value", "ok"))
        .assertNext(response -> assertThat(response).isEqualTo("value=ok"))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void patchStringValue() {
    StepVerifier.create(apiClient.patchStringValue("name", "suffix", "payload"))
        .expectNextCount(0)
        .verifyComplete();

    StepVerifier.create(apiClient.patchStringValue("name", "exception", "payload"))
        .expectError(RestApiResponseException.class)
        .verifyThenAssertThat();
  }

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