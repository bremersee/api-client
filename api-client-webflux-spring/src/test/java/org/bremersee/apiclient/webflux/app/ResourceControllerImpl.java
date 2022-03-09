package org.bremersee.apiclient.webflux.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ResourceControllerImpl implements ResourceController {

  @Override
  public Mono<String> postResource(Resource resource) {
    try {
      return Mono.just(new String(
          FileCopyUtils.copyToByteArray(resource.getInputStream()),
          StandardCharsets.UTF_8));

    } catch (IOException exception) {
      throw new IoRuntimeException("Creating string from resource failed", exception);
    }
  }
}
