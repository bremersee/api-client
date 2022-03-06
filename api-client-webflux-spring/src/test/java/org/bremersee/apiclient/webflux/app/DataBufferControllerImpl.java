package org.bremersee.apiclient.webflux.app;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DataBufferControllerImpl implements DataBufferController {

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
}
