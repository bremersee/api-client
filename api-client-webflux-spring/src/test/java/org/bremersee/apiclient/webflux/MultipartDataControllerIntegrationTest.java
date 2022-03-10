package org.bremersee.apiclient.webflux;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.app.MultipartDataController;
import org.bremersee.apiclient.webflux.app.TestConfiguration;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.bremersee.apiclient.webflux.contract.spring.multipart.PartBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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
@Slf4j
class MultipartDataControllerIntegrationTest {

  private static final String FORM_FIELD_NAME = "string";

  private static final String FORM_FIELD_VALUE = "Hello sun!";

  private static final String FILE_PART_NAME = "resource";

  private static final String FILE_PART_RESOURCE = "text.txt";

  private static final String FILE_PART_CONTENT = "Hello world!";

  private static final String DATA_BUFFER_PART_NAME = "buf";

  private static final String REAL_FILES_PART_NAME = "files";

  final PartBuilder partBuilder = new PartBuilder();

  Path tmpFile;

  @LocalServerPort
  int port;

  WebClient webClient;

  MultipartDataController apiClient;

  private static Map<String, Object> expected() {
    return expected(null, null);
  }

  private static Map<String, Object> expected(byte[] dataBufferBytes, byte[] fileBytes) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(FORM_FIELD_NAME, FORM_FIELD_VALUE);
    map.put(FILE_PART_NAME, FILE_PART_CONTENT);
    if (!isEmpty(dataBufferBytes)) {
      map.put(DATA_BUFFER_PART_NAME, toString(dataBufferBytes));
    }
    if (!isEmpty(fileBytes)) {
      map.put(REAL_FILES_PART_NAME, toString(fileBytes));
    }
    return map;
  }

  private static byte[] randomBytes(int len) {
    Random random = new Random();
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    return bytes;
  }

  private static String toString(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  private Flux<DataBuffer> toDataBuffer(byte[] bytes, int bufferSize) {
    return DataBufferUtils.read(new ByteArrayResource(bytes), new DefaultDataBufferFactory(), bufferSize);
  }

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

  @AfterEach
  void deleteTmpFile() {
    if (nonNull(tmpFile)) {
      try {
        Files.delete(tmpFile);
        tmpFile = null;
      } catch (IOException e) {
        throw new IllegalStateException("Deleting tmp file failed.", e);
      }
    }
  }

  private void writeToTmpFile(byte[] bytes) {
    try {
      tmpFile = Files.createTempFile("apiclient", "java");
      Files.copy(new ByteArrayInputStream(bytes), tmpFile, StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      throw new IllegalStateException("Creating tmp file failed.", e);
    }
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
            .containsExactlyInAnyOrderEntriesOf(expected()))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postMultipartDataMap() {
    MultiValueMap<String, Part> partMap = new LinkedMultiValueMap<>();
    partMap.add(
        FORM_FIELD_NAME,
        partBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build());
    partMap.add(
        FILE_PART_NAME,
        partBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build());

    byte[] dataBuf = randomBytes(16 * 1024 + 8);
    partMap.add(
        DATA_BUFFER_PART_NAME,
        partBuilder.part(DATA_BUFFER_PART_NAME, toDataBuffer(dataBuf, 256))
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .build());

    byte[] fileBytes = randomBytes(3000);
    writeToTmpFile(fileBytes);
    partMap.add(
        REAL_FILES_PART_NAME,
        partBuilder.part(REAL_FILES_PART_NAME, tmpFile)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .build());

    StepVerifier.create(apiClient.postMultipartDataMap(partMap))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(expected(dataBuf, fileBytes)))
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
            .containsExactlyInAnyOrderEntriesOf(expected()))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postMonoMultipartDataMap() {
    MultiValueMap<String, Part> partMap = new LinkedMultiValueMap<>();
    partMap.add(
        FORM_FIELD_NAME,
        partBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build());
    partMap.add(
        FILE_PART_NAME,
        partBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build());

    StepVerifier.create(apiClient.postMonoMultipartDataMap(Mono.just(partMap)))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(expected()))
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
            .containsExactlyInAnyOrderEntriesOf(expected()))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postParts() {
    Part stringPart = partBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build();
    Part resourcePart = partBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build();

    StepVerifier.create(apiClient.postParts(stringPart, resourcePart, null, null))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(expected()))
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
            .containsExactlyInAnyOrderEntriesOf(expected()))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postMonoParts() {
    Part stringPart = partBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build();
    Part resourcePart = partBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build();

    StepVerifier.create(apiClient
            .postMonoParts(Mono.just(stringPart), Mono.just(resourcePart), Mono.empty(), Mono.empty()))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(expected()))
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
            .containsExactlyInAnyOrderEntriesOf(expected()))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void postFluxParts() {
    List<Part> parts = new ArrayList<>();
    parts.add(
        partBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build());
    parts.add(
        partBuilder.part(FILE_PART_NAME, new ClassPathResource(FILE_PART_RESOURCE)).build());

    byte[] dataBuf = randomBytes(7 * 1024 + 7);
    parts.add(
        partBuilder.part(DATA_BUFFER_PART_NAME, toDataBuffer(dataBuf, 128))
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .build());

    byte[] fileBytes = randomBytes(1234);
    writeToTmpFile(fileBytes);
    parts.add(
        partBuilder.part(REAL_FILES_PART_NAME, tmpFile)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .build());

    StepVerifier.create(apiClient.postFluxParts(Flux.fromStream(parts.stream())))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(expected(dataBuf, fileBytes)))
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
            .containsExactlyInAnyOrderEntriesOf(expected()))
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

  @Test
  void postNamedFluxParts() {
    List<Part> parts = new ArrayList<>();
    parts.add(
        partBuilder.part("parts", new ClassPathResource(FILE_PART_RESOURCE)).build());

    // This part will be ignored, because it's name is not 'parts'.
    parts.add(
        partBuilder.part(FORM_FIELD_NAME, FORM_FIELD_VALUE).contentType(MediaType.TEXT_PLAIN).build());

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("parts", new ClassPathResource(FILE_PART_RESOURCE));

    StepVerifier
        .create(apiClient.postNamedFluxParts(Flux.fromStream(parts.stream())))
        .assertNext(response -> assertThat(response)
            .containsExactlyInAnyOrderEntriesOf(Map.of("parts", FILE_PART_CONTENT)))
        .expectNextCount(0)
        .verifyComplete();
  }

  private static class MapRef extends ParameterizedTypeReference<Map<String, Object>> {

  }

}