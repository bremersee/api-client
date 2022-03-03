package org.bremersee.apiclient.webflux.function.resolver;

import java.util.Collection;
import java.util.List;
import org.bremersee.apiclient.webflux.function.resolver.spring.CookieResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CookiesResolver extends AbstractInvocationResolver<String, MultiValueMap<String, String>> {

  public CookiesResolver() {
    this(List.of(new CookieResolver()));
  }

  public CookiesResolver(
      Collection<? extends AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>> resolvers) {
    super(resolvers);
  }

  @Override
  public MultiValueMap<String, String> get() {
    return new LinkedMultiValueMap<>();
  }

}
