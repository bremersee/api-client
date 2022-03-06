package org.bremersee.apiclient.webflux.app;

import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PublisherControllerImpl implements PublisherController{

  @Override
  public Mono<String> postPublisher(Publisher<String> publisher) {
    return Mono.from(publisher);
  }
}
