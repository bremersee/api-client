package org.bremersee.apiclient.webflux.function.resolver;

import java.util.Collection;
import java.util.List;
import org.bremersee.apiclient.webflux.function.resolver.spring.RequestHeaderResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RequestHeadersResolver extends AbstractInvocationResolver<String, MultiValueMap<String, String>> {

  public RequestHeadersResolver() {
    this(List.of(new RequestHeaderResolver()));
  }

  public RequestHeadersResolver(
      Collection<? extends AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>> resolvers) {
    super(resolvers);
  }

  @Override
  public MultiValueMap<String, String> get() {
    return new LinkedMultiValueMap<>();
  }

}
