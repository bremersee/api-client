package org.bremersee.apiclient.webflux.function.resolver;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.springframework.util.MultiValueMap;

public abstract class AbstractInvocationResolver<E, T extends MultiValueMap<String, E>>
    implements Supplier<T>, Function<Invocation, T> {

  private final List<InvocationParameterResolver<E, T>> resolvers = new ArrayList<>();

  public AbstractInvocationResolver(Collection<? extends InvocationParameterResolver<E, T>> resolvers) {
    if (nonNull(resolvers)) {
      this.resolvers.addAll(resolvers);
    }
  }

  protected Optional<InvocationParameterResolver<E, T>> findResolver(InvocationParameter invocationParameter) {
    return resolvers.stream()
        .filter(resolver -> resolver.canResolve(invocationParameter))
        .findFirst();
  }

  @Override
  public T apply(Invocation invocation) {

    return invocation.toMethodParameterStream()
        .map(invocationParameter -> findResolver(invocationParameter)
            .map(resolver -> {
              T parameterMap = get();
              parameterMap.putAll(resolver.resolve(invocationParameter));
              return parameterMap;
            }))
        .flatMap(Optional::stream)
        .reduce((first, second) -> {
          first.addAll(second);
          return first;
        })
        .orElseGet(this);
  }
}
