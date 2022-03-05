package org.bremersee.apiclient.webflux.function;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public enum HttpRequestMethod {
  GET(WebClient::get),
  HEAD(WebClient::head),
  POST(WebClient::post),
  PUT(WebClient::put),
  PATCH(WebClient::patch),
  DELETE(WebClient::delete),
  OPTIONS(WebClient::options);
  //TRACE();

  private final Function<WebClient, RequestHeadersUriSpec<?>> uriSpecFunction;

  HttpRequestMethod(Function<WebClient, RequestHeadersUriSpec<?>> uriSpecFunction) {
    this.uriSpecFunction = uriSpecFunction;
  }

  public RequestHeadersUriSpec<?> invoke(WebClient webClient) {
    return uriSpecFunction.apply(webClient);
  }

  public static Optional<HttpRequestMethod> resolve(String method) {
    return Arrays.stream(HttpRequestMethod.values())
        .filter(httpRequestMethod -> httpRequestMethod.name().equalsIgnoreCase(method))
        .findFirst();
  }
}
