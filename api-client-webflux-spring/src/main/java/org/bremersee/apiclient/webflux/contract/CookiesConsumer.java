package org.bremersee.apiclient.webflux.contract;

import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.springframework.util.MultiValueMap;

@Value.Immutable
@Valid
public interface CookiesConsumer extends BiConsumer<Invocation, MultiValueMap<String, String>> {

  static ImmutableCookiesConsumer.Builder builder() {
    return ImmutableCookiesConsumer.builder();
  }

  @NotNull
  Function<Invocation, MultiValueMap<String, String>> getCookiesResolver();

  @Override
  default void accept(Invocation invocation, MultiValueMap<String, String> cookies) {
    cookies.addAll(getCookiesResolver().apply(invocation));
  }

}
