package org.bremersee.apiclient.webflux.spring.boot.autoconfigure.app;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping(path = "/api")
public interface ControllerApi {

  @PostMapping(path = "/data", consumes = MediaType.ALL_VALUE)
  Mono<String> postData(@RequestBody Flux<DataBuffer> data);

  @PostMapping(
      path = "/multipart/parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<String> postParts(
      @RequestPart(name = "string") FormFieldPart stringPart);

  @RequestMapping(path = "/publisher", method = RequestMethod.POST, consumes = "text/*")
  Mono<String> postPublisher(@RequestBody Publisher<String> publisher);

  @RequestMapping(path = "/resource", method = RequestMethod.POST, consumes = "text/*")
  Mono<String> postResource(@RequestBody Resource resource);

  @PutMapping(path = "/value/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.TEXT_PLAIN_VALUE)
  Mono<Map<String, Object>> putStringValue(
      @PathVariable("name") String name,
      @RequestBody String payload);

}
