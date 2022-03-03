package org.bremersee.apiclient.webflux.function.resolver;

import java.util.Collection;
import java.util.List;
import org.bremersee.apiclient.webflux.function.resolver.spring.PathVariableResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PathVariablesResolver extends AbstractInvocationResolver<Object, MultiValueMap<String, Object>> {

  public PathVariablesResolver() {
    this(List.of(new PathVariableResolver()));
  }

  public PathVariablesResolver(
      Collection<? extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>> resolvers) {
    super(resolvers);
  }

  @Override
  public MultiValueMap<String, Object> get() {
    return new LinkedMultiValueMap<>();
  }

}
