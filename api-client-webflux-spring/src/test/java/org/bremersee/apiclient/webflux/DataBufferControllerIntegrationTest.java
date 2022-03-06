package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.DataBufferController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
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
class DataBufferControllerIntegrationTest {

  @LocalServerPort
  int port;

  WebClient webClient;

  DataBufferController apiClient;

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
        .build(DataBufferController.class);
    webClient = WebClient.builder()
        .baseUrl(baseUrl())
        .build();
  }

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

  @Test
  void postResource() {
    StepVerifier
        .create(apiClient.postData(DataBufferUtils
            .read(new ClassPathResource("text.txt"), new DefaultDataBufferFactory(), 256)))
        .assertNext(response -> assertThat(response).isEqualTo("Hello world!"))
        .expectNextCount(0)
        .verifyComplete();
  }

}