package org.bremersee.apiclient.webflux.contract;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

@Value.Immutable
@Valid
public interface RequestUriSpecFunction
    extends BiFunction<Invocation, WebClient, RequestHeadersUriSpec<?>> {

  static ImmutableRequestUriSpecFunction.Builder builder() {
    return ImmutableRequestUriSpecFunction.builder();
  }

  @NotNull
  Function<Invocation, HttpRequestMethod> getHttpMethodResolver();

  @Override
  default RequestHeadersUriSpec<?> apply(Invocation invocation, WebClient webClient) {

    Assert.notNull(invocation, "Invocation must be present.");
    Assert.notNull(webClient, "Web client must be present.");
    return Optional.ofNullable(getHttpMethodResolver().apply(invocation))
        .map(httpRequestMethod -> httpRequestMethod.invoke(webClient))
        .orElseThrow(() -> new IllegalStateException(
            String.format("Cannot find request method on method '%s'.", invocation.getMethod().getName())));
  }

}
