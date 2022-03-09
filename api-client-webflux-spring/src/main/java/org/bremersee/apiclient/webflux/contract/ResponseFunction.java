package org.bremersee.apiclient.webflux.contract;

import static java.util.Objects.nonNull;
import static org.springframework.core.GenericTypeResolver.resolveReturnTypeArgument;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import org.bremersee.apiclient.webflux.Invocation;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseFunction implements BiFunction<Invocation, ResponseSpec, Publisher<?>> {

  @Override
  public Publisher<?> apply(Invocation invocation, ResponseSpec responseSpec) {

    Method method = invocation.getMethod();
    Class<?> responseClass = method.getReturnType();
    if (Mono.class.isAssignableFrom(responseClass)) {
      Class<?> typeClass = resolveReturnTypeArgument(method, responseClass);
      return nonNull(typeClass)
          ? responseSpec.bodyToMono(typeClass)
          : responseSpec.bodyToMono(responseClass);
    }
    if (Flux.class.isAssignableFrom(responseClass)) {
      Class<?> typeClass = resolveReturnTypeArgument(method, responseClass);
      return nonNull(typeClass)
          ? responseSpec.bodyToFlux(typeClass)
          : responseSpec.bodyToFlux(responseClass);
    }
    if (Publisher.class.isAssignableFrom(responseClass)) {
      Class<?> typeClass = resolveReturnTypeArgument(method, responseClass);
      return nonNull(typeClass)
          ? responseSpec.bodyToFlux(typeClass)
          : responseSpec.bodyToFlux(responseClass);
    }
    throw new IllegalStateException(
        "Response class must be Mono, Flux or Publisher.");
  }
}
