package org.bremersee.apiclient.webflux.app;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping(path = "/api")
public interface DataBufferController {

  @PostMapping(path = "/data", consumes = MediaType.ALL_VALUE)
  Mono<String> postData(@RequestBody Flux<DataBuffer> data);

}
