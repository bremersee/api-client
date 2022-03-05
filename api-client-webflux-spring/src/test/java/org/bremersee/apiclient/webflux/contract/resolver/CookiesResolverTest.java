package org.bremersee.apiclient.webflux.contract.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class CookiesResolverTest {

  @Test
  void get() {
    CookiesResolver target = new CookiesResolver(List.of());
    assertThat(target.get())
        .isEmpty();
  }
}