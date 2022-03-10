package org.bremersee.apiclient.webflux.spring.boot.autoconfigure.app;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ControllerImpl implements ControllerApi {

  @Override
  public Mono<String> postData(Flux<DataBuffer> data) {

    return DataBufferUtils.join(data)
        .map(dataBuffer -> {
          byte[] bytes = new byte[dataBuffer.readableByteCount()];
          dataBuffer.read(bytes);
          DataBufferUtils.release(dataBuffer);
          return bytes;
        })
        .map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  @Override
  public Mono<String> postParts(FormFieldPart stringPart) {
    return Mono.just(stringPart.value());
  }

  @Override
  public Mono<String> postPublisher(Publisher<String> publisher) {
    return Mono.from(publisher);
  }

  @Override
  public Mono<String> postResource(Resource resource) {
    try {
      byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
      return Mono.just(new String(bytes, StandardCharsets.UTF_8));

    } catch (Exception e) {
      return Mono.error(e);
    }
  }

  @Override
  public Mono<Map<String, Object>> putStringValue(String name, String payload) {
    return Mono.just(Map.of(
        "name", name,
        "payload", payload
    ));
  }

  @GetMapping(path = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map<String, Object>> getPage(
      @RequestParam("page") int page,
      @RequestParam("size") int size,
      @RequestParam("sort") List<String> sort) {
    return Mono.just(Map.of(
        "page", page,
        "size", size,
        "sort", sort
    ));
  }
}
