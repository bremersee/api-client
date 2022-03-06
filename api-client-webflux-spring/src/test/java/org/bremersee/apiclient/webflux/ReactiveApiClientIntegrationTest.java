package org.bremersee.apiclient.webflux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.ControllerOne;
import org.bremersee.apiclient.webflux.app.ControllerTwo;
import org.bremersee.apiclient.webflux.app.FormDataController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.exception.RestApiResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
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
@AutoConfigureWebTestClient
@Slf4j
class ReactiveApiClientIntegrationTest {

  @LocalServerPort
  int port;

  @Autowired
  WebTestClient webClient;

  String baseUrl() {
    return "http://localhost:" + port;
  }

  WebClient newWebClient() {
    return WebClient.builder()
        .baseUrl(baseUrl())
        .build();
  }

  private ControllerOne newControllerOneClient() {
    return new ReactiveApiClient(WebClient.builder(), null, null) // TODO
        .newInstance(ControllerOne.class, baseUrl());
  }

  private ControllerTwo newControllerTwoClient() {
    return ReactiveApiClient.builder()
        .webClient(newWebClient())
        .build(ControllerTwo.class);
  }

  private FormDataController newFormDataController() {
    return ReactiveApiClient.builder()
        .webClient(newWebClient())
        .build(FormDataController.class);
  }

  /**
   * Call object methods.
   */
  @Test
  void callObjectMethods() {
    ControllerOne proxy = newControllerOneClient();
    assertEquals(proxy.hashCode(), proxy.hashCode());
    assertNotEquals(proxy, null);
    assertNotNull(proxy.toString());
  }

  /**
   * Call with web test client.
   */
  @Test
  void callWithWebTestClient() {
    webClient.get().uri("/").exchange().expectStatus().isOk().expectBody(String.class)
        .isEqualTo(ControllerOne.OK_RESPONSE);
  }

  /**
   * Call with web client.
   */
  @Test
  void callWithWebClient() {
    StepVerifier
        .create(newWebClient().get().uri(UriBuilder::build).retrieve().bodyToMono(String.class))
        .assertNext(response -> Assertions.assertEquals(ControllerOne.OK_RESPONSE, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Simple get.
   */
  @Test
  void simpleGet() {
    StepVerifier.create(newControllerOneClient().simpleGet())
        .assertNext(response -> Assertions.assertEquals(ControllerOne.OK_RESPONSE, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Do get.
   */
  @Test
  void doGet() {
    StepVerifier.create(newControllerOneClient().getOks())
        .assertNext(ok -> assertEquals("OK_0", ok.get("value")))
        .assertNext(ok -> assertEquals("OK_1", ok.get("value")))
        .assertNext(ok -> assertEquals("OK_2", ok.get("value")))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Do post.
   */
  @Test
  void doPost() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("value", "ok");
    StepVerifier.create(newFormDataController().addOk(form))
        .assertNext(response -> Assertions.assertEquals(ControllerOne.OK_RESPONSE, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Do put.
   */
  @Test
  void doPut() {
    StepVerifier.create(newControllerOneClient().updateOk("value", "ok"))
        .assertNext(response -> assertEquals("value=ok", response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Do patch.
   */
  @Test
  void doPatch() {
    StepVerifier.create(newControllerOneClient().patchOk("name", "suffix", "payload"))
        .expectNextCount(0)
        .verifyComplete();

    StepVerifier.create(newControllerOneClient().patchOk("name", "exception", "payload"))
        .expectError(RestApiResponseException.class)
        .verifyThenAssertThat();
  }

  /**
   * Do delete.
   */
  @Test
  void doDelete() {
    StepVerifier.create(newControllerOneClient().deleteOk("value"))
        .assertNext(Assertions::assertTrue)
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Upload.
   *
  @Test
  void upload() {
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("k0", "v0");
    data.add("k1", "v1");

    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    map.put("x-ok-flag", "a-flag");
    map.put("last", "a-value");
    map.putAll(data);

    StepVerifier.create(newFormDataController().upload("a-flag", "a-value", data))
        .assertNext(response -> assertEquals(map, response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Say hello with name.
   */
  @Test
  void sayHelloWithName() {
    StepVerifier.create(newControllerTwoClient().sayHello("Anna"))
        .assertNext(response -> assertEquals("Hello Anna", response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Say hello without name.
   */
  @Test
  void sayHelloWithoutName() {
    StepVerifier.create(newControllerTwoClient().sayHello(null))
        .assertNext(response -> assertEquals("Hello Tom", response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Say hello to.
   */
  @Test
  void sayHelloTo() {
    StepVerifier.create(newControllerTwoClient().sayHelloTo("Anna Livia"))
        .assertNext(response -> assertEquals("Hello Anna Livia", response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Sets name.
   */
  @Test
  void setName() {
    StepVerifier.create(newControllerTwoClient().setName("Anna Livia"))
        .assertNext(response -> assertEquals("Anna Livia", response))
        .expectNextCount(0)
        .verifyComplete();
  }

  /**
   * Sets no name.
   */
  @Test
  void setNoName() {
    StepVerifier.create(newControllerTwoClient().setName(null))
        .assertNext(response -> assertEquals("null", response))
        .expectNextCount(0)
        .verifyComplete();
  }

}