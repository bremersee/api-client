package org.bremersee.apiclient.webflux;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.ControllerTwo;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.app.ValueController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
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

  private ControllerTwo newControllerTwoClient() {
    return ReactiveApiClient.builder()
        .webClient(newWebClient())
        .build(ControllerTwo.class);
  }

  /**
   * Call with web test client.
   */
  @Test
  void callWithWebTestClient() {
    webClient.get().uri("/").exchange().expectStatus().isOk().expectBody(String.class)
        .isEqualTo(ValueController.STRING_VALUE);
  }

  /**
   * Call with web client.
   */
  @Test
  void callWithWebClient() {
    StepVerifier
        .create(newWebClient().get().uri(UriBuilder::build).retrieve().bodyToMono(String.class))
        .assertNext(response -> Assertions.assertEquals(ValueController.STRING_VALUE, response))
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