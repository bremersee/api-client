package org.bremersee.apiclient.webflux.function.resolver;

import java.util.Collection;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RequestParametersResolver extends AbstractInvocationResolver<Object, MultiValueMap<String, Object>> {

  public RequestParametersResolver(
      Collection<? extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>> resolvers) {
    super(resolvers);
  }

  @Override
  public MultiValueMap<String, Object> get() {
    return new LinkedMultiValueMap<>();
  }

}
