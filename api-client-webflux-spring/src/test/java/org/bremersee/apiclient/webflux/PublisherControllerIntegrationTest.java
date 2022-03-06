package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.PublisherController;
import org.bremersee.apiclient.webflux.app.ResourceController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
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
class PublisherControllerIntegrationTest {

  @LocalServerPort
  int port;

  PublisherController apiClient;

  String baseUrl() {
    return "http://localhost:" + port;
  }

  @BeforeEach
  void init() {
    apiClient = ReactiveApiClient.builder()
        .webClient(WebClient.builder()
            .baseUrl(baseUrl())
            .build())
        .contract(new ReactiveSpringContract())
        .errorHandler(ReactiveErrorHandler.builder().build())
        .build(PublisherController.class);
  }

  @Test
  void postPublisher() {
    StepVerifier
        .create(apiClient.postPublisher(Mono.just("Hello world!")))
        .assertNext(response -> assertThat(response).isEqualTo("Hello world!"))
        .expectNextCount(0)
        .verifyComplete();
  }

}