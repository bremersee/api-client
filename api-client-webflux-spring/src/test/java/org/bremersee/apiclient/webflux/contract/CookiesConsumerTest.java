package org.bremersee.apiclient.webflux.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class CookiesConsumerTest {

  @Test
  void accept() {
    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, String>> cookiesResolver = mock(Function.class);
    MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
    expected.add("a", "b");
    when(cookiesResolver.apply(any()))
        .thenReturn(expected);
    CookiesConsumer target = CookiesConsumer.builder()
        .cookiesResolver(cookiesResolver)
        .build();
    Invocation invocation = mock(Invocation.class);
    MultiValueMap<String, String> actual = new LinkedMultiValueMap<>();
    target.accept(invocation, actual);
    assertThat(actual)
        .isEqualTo(expected);
  }
}