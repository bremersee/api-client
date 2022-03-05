package org.bremersee.apiclient.webflux.contract.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RequestParametersResolverTest {

  @Test
  void get() {
    RequestParametersResolver target = new RequestParametersResolver(List.of());
    assertThat(target.get())
        .isEmpty();
  }
}