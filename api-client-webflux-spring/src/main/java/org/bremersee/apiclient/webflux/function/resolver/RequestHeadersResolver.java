package org.bremersee.apiclient.webflux.function.resolver;

import java.util.Collection;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RequestHeadersResolver extends AbstractInvocationResolver<String, MultiValueMap<String, String>> {

  public RequestHeadersResolver(
      Collection<? extends AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>> resolvers) {
    super(resolvers);
  }

  @Override
  public MultiValueMap<String, String> get() {
    return new LinkedMultiValueMap<>();
  }

}
