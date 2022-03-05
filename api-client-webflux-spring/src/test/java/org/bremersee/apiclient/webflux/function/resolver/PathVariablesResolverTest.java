package org.bremersee.apiclient.webflux.function.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PathVariablesResolverTest {

  @Test
  void get() {
    PathVariablesResolver target = new PathVariablesResolver(List.of());
    assertThat(target.get())
        .isEmpty();
  }
}