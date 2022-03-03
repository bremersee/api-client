package org.bremersee.apiclient.webflux.function.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.springframework.util.MultiValueMap;

public abstract class AbstractInvocationResolver<E, T extends MultiValueMap<String, E>>
    implements Supplier<T>, Function<Invocation, T> {

  private final List<InvocationParameterResolver<E, T>> resolvers = new ArrayList<>();

  public AbstractInvocationResolver() {
  }

  public AbstractInvocationResolver(Collection<? extends InvocationParameterResolver<E, T>> resolvers) {
    Optional.ofNullable(resolvers).stream().flatMap(Collection::stream).forEach(this::addResolver);
  }

  public List<InvocationParameterResolver<E, T>> getResolvers() {
    return List.copyOf(resolvers);
  }

  public AbstractInvocationResolver<E, T> addResolver(InvocationParameterResolver<E, T> resolver) {
    return addResolver(resolvers.size(), resolver);
  }

  public AbstractInvocationResolver<E, T> addResolver(
      int index,
      InvocationParameterResolver<E, T> resolver) {

    Supplier<String> f;
    if (Objects.nonNull(resolver)) {
      int validIndex = Math.min(Math.abs(index), resolvers.size());
      resolvers.add(validIndex, resolver);
    }
    return this;
  }

  protected Optional<InvocationParameterResolver<E, T>> findResolver(InvocationParameter invocationParameter) {
    return getResolvers().stream()
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
          first.putAll(second);
          return first;
        })
        .orElseGet(this);
  }
}
