package org.bremersee.apiclient.webflux;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.immutables.value.Value;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

@Value.Immutable
@Valid
public interface ReactiveContract {

  static ImmutableReactiveContract.Builder builder() {
    return ImmutableReactiveContract.builder();
  }

  @NotNull
  BiConsumer<Invocation, MultiValueMap<String, String>> getCookiesConsumer();

  @NotNull
  BiConsumer<Invocation, HttpHeaders> getHeadersConsumer();

  @NotNull
  BiFunction<Invocation, UriBuilder, URI> getRequestUriFunction();

  @NotNull
  BiFunction<Invocation, WebClient, RequestHeadersUriSpec<?>> getRequestUriSpecFunction();

  @NotNull
  BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> getRequestBodyInserterFunction();

  @NotNull
  BiFunction<Invocation, ResponseSpec, Publisher<?>> getResponseFunction();

}
