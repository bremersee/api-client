package org.bremersee.apiclient.webflux.function.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RequestHeadersResolverTest {

  @Test
  void get() {
    RequestHeadersResolver target = new RequestHeadersResolver(List.of());
    assertThat(target.get())
        .isEmpty();
  }
}