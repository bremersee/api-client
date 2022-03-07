package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.MultipartDataController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.bremersee.apiclient.webflux.contract.spring.multipart.PartBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
class MultipartDataControllerIntegrationTest {

  private static final String FORM_FIELD_NAME = "string";

  private static final String FORM_FIELD_VALUE = "Hello sun!";

  private static final String FILE_PART_NAME = "resource";

  private static final String FILE_PART_RESOURCE = "text.txt";

  private static final String FILE_PART_CONTENT = "Hello world!";

  private static final Map<String, Object> EXPECTED = Map.of(
      FORM_FIELD_NAME, FORM_FIELD_VALUE,
      FILE_PART_NAME, FILE_PART_CONTENT
  );

  @LocalServerPort
  int port;

  WebClient webClient;

  MultipartDataController apiClient;

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
        .build(MultipartDataController.class);
    webClient = WebClient.builder()
        .baseUrl(baseUrl())
        .build();
  }

  @Test
  void postMultipartDataMapWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE, MediaType.TEXT_PLAIN);
    builder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE));
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/map")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postMultipartDataMap() {
    MultiValueMap<String, Part> partMap = new LinkedMultiValueMap<>();
    partMap.add(
        FORM_FIELD_NAME,
        PartBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build());
    partMap.add(
        FILE_PART_NAME,
        PartBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build());

    StepVerifier.create(apiClient.postMultipartDataMap(partMap))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postMonoMultipartDataMapWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE, MediaType.TEXT_PLAIN);
    builder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE));
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/mono-map")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Disabled
  @Test
  void postMonoMultipartDataMap() {
    // TODO fails
    MultiValueMap<String, Part> partMap = new LinkedMultiValueMap<>();
    partMap.add(
        FORM_FIELD_NAME,
        PartBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build());
    partMap.add(
        FILE_PART_NAME,
        PartBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build());

    StepVerifier.create(apiClient.postMonoMultipartDataMap(Mono.just(partMap)))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postPartsWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE, MediaType.TEXT_PLAIN);
    builder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE));
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/parts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Disabled
  @Test
  void postParts() {
    Part stringPart = PartBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build();
    Part resourcePart = PartBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build();

    StepVerifier.create(apiClient.postParts(stringPart, resourcePart))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postMonoPartsWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE, MediaType.TEXT_PLAIN);
    builder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE));
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/mono-parts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postFluxPartsWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE, MediaType.TEXT_PLAIN);
    builder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE));
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/flux-parts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postFluxPartsAsyncWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE, MediaType.TEXT_PLAIN);
    builder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE));
    //noinspection Convert2Diamond
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/flux-parts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromPublisher(Mono.just(builder.build()),
                new ParameterizedTypeReference<MultiValueMap<String, HttpEntity<?>>>() {
                }))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(EXPECTED))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postNamedFluxPartsWithWebClient() {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("parts", new ClassPathResource(FILE_PART_RESOURCE));
    StepVerifier
        .create(webClient
            .post()
            .uri("/api/multipart/named-flux-parts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(new MapRef()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(Map.of("parts", FILE_PART_CONTENT)))
        .expectNextCount(0)
        .verifyComplete();
  }

  private static class MapRef extends ParameterizedTypeReference<Map<String, Object>> {

  }

}