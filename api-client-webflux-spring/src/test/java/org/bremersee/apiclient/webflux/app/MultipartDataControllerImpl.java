package org.bremersee.apiclient.webflux.app;

import static java.util.Objects.nonNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@RestController
public class MultipartDataControllerImpl implements MultipartDataController {

  private Mono<String> content(Part part) {
    return DataBufferUtils.join(part.content())
        .map(dataBuffer -> {
          byte[] bytes = new byte[dataBuffer.readableByteCount()];
          dataBuffer.read(bytes);
          DataBufferUtils.release(dataBuffer);
          return bytes;
        })
        .map(bytes -> {
          if (MediaType.APPLICATION_OCTET_STREAM.isCompatibleWith(part.headers().getContentType())) {
            return Base64.getEncoder().encodeToString(bytes);
          }
          return new String(bytes, StandardCharsets.UTF_8).trim();
        });
  }

  @Override
  public Mono<Map<String, Object>> postMultipartDataMap(MultiValueMap<String, Part> data) {
    return Flux.fromStream(data.entrySet().stream())
        .flatMap(entry -> content(entry.getValue().get(0)).map(content -> Tuples.of(entry.getKey(), content)))
        .collectMap(Tuple2::getT1, Tuple2::getT2);
  }

  @Override
  public Mono<Map<String, Object>> postMonoMultipartDataMap(Mono<MultiValueMap<String, Part>> data) {
    return data.flatMap(map -> Flux.fromStream(map.entrySet().stream())
        .flatMap(entry -> content(entry.getValue().get(0)).map(content -> Tuples.of(entry.getKey(), content)))
        .collectMap(Tuple2::getT1, Tuple2::getT2));
  }

  @Override
  public Mono<Map<String, Object>> postParts(
      Part stringPart,
      Part resourcePart,
      Part dataBufferPart,
      Part filePart) {

    List<Part> parts = new ArrayList<>();
    parts.add(stringPart);
    parts.add(resourcePart);
    if (nonNull(dataBufferPart)) {
      parts.add(dataBufferPart);
    }
    if (nonNull(filePart)) {
      parts.add(filePart);
    }
    return Flux.fromStream(parts.stream())
        .flatMap(part -> content(part).map(str -> Tuples.of(part.name(), str)))
        .collectMap(Tuple2::getT1, Tuple2::getT2);
  }

  @Override
  public Mono<Map<String, Object>> postMonoParts(
      Mono<Part> stringPart,
      Mono<Part> resourcePart) {
    return Flux.concat(stringPart, resourcePart)
        .flatMap(part -> content(part).map(str -> Tuples.of(part.name(), str)))
        .collectMap(Tuple2::getT1, Tuple2::getT2);
  }

  @Override
  public Mono<Map<String, Object>> postFluxParts(
      Flux<Part> parts) {
    return parts
        .flatMap(part -> content(part).map(str -> Tuples.of(part.name(), str)))
        .collectMap(Tuple2::getT1, Tuple2::getT2);
  }

  @Override
  public Mono<Map<String, Object>> postNamedFluxParts(
      Flux<Part> parts) {
    return parts
        .flatMap(part -> content(part).map(str -> Tuples.of(part.name(), str)))
        .collectMap(Tuple2::getT1, Tuple2::getT2);
  }

}
