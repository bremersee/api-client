package org.bremersee.apiclient.webflux.app;

import org.reactivestreams.Publisher;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@RequestMapping(path = "/api")
public interface PublisherController {

  @RequestMapping(path = "/publisher", method = RequestMethod.POST, consumes = "text/*")
  Mono<String> postPublisher(@RequestBody Publisher<String> publisher);

}
