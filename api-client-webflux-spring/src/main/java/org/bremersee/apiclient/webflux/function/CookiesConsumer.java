package org.bremersee.apiclient.webflux.function;

import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import org.bremersee.apiclient.webflux.function.resolver.CookiesResolver;
import org.springframework.util.MultiValueMap;

@Builder(toBuilder = true)
public class CookiesConsumer implements BiConsumer<Invocation, MultiValueMap<String, String>> {

  @NonNull
  private Function<Invocation, MultiValueMap<String, String>> cookiesResolver;

  @Override
  public void accept(Invocation invocation, MultiValueMap<String, String> cookies) {
    cookies.addAll(cookiesResolver.apply(invocation));
  }

}
