package org.bremersee.apiclient.webflux;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.immutables.value.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Value.Immutable
@Valid
public interface ReactiveErrorHandler {

  static ImmutableReactiveErrorHandler.Builder builder() {
    return ImmutableReactiveErrorHandler.builder();
  }

  @Value.Default
  @NotNull
  default Predicate<HttpStatus> getErrorPredicate() {
    return HttpStatus::isError;
  }

  @Nullable
  Function<ClientResponse, Mono<? extends Throwable>> getErrorFunction();

}
