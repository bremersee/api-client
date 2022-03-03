package org.bremersee.apiclient.webflux;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.exception.webclient.DefaultWebClientErrorDecoder;
import org.immutables.value.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Value.Immutable
@Valid
public interface ErrorFunctionBundle {

  static ImmutableErrorFunctionBundle.Builder builder() {
    return ImmutableErrorFunctionBundle.builder();
  }

  @Value.Default
  @NotNull
  default Predicate<HttpStatus> getErrorPredicate() {
    return HttpStatus::isError;
  }

  @Value.Default
  @NotNull
  default Function<ClientResponse, Mono<? extends Throwable>> getErrorFunction() {
    return new DefaultWebClientErrorDecoder();
  }

}
