package org.bremersee.apiclient.webflux.spring.boot.autoconfigure;

import org.springframework.web.reactive.function.client.WebClient;

public interface ReactiveApiClientWebClientBuilderConfigurer {

  void configure(WebClient.Builder webClientBuilder);

}
