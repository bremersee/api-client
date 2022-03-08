package org.bremersee.apiclient.webflux.app;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MultipartDataController {

  @PostMapping(
      path = "/api/multipart/map",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postMultipartDataMap(@RequestBody MultiValueMap<String, Part> data);

  @PostMapping(
      path = "/api/multipart/mono-map",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postMonoMultipartDataMap(@RequestBody Mono<MultiValueMap<String, Part>> monoData);

  @PostMapping(
      path = "/api/multipart/parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postParts(
      @RequestPart(name = "string") Part stringPart,
      @RequestPart(name = "resource") Part resourcePart,
      @RequestPart(name = "buf", required = false) Part dataBufferPart,
      @RequestPart(name = "files", required = false) Part filePart);

  @PostMapping(
      path = "/api/multipart/mono-parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postMonoParts(
      @RequestPart(name = "string") Mono<Part> stringPart,
      @RequestPart(name = "resource") Mono<Part> resourcePart,
      @RequestPart(name = "buf", required = false) Mono<Part> dataBufferPart,
      @RequestPart(name = "files", required = false) Mono<Part> filePart);

  @PostMapping(
      path = "/api/multipart/flux-parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postFluxParts(
      @RequestBody Flux<Part> parts);

  @PostMapping(
      path = "/api/multipart/named-flux-parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postNamedFluxParts(
      @RequestPart(name = "parts") Flux<Part> parts);

}
