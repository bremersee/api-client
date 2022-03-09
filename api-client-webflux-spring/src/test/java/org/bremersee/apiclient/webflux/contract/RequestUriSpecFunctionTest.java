package org.bremersee.apiclient.webflux.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

class RequestUriSpecFunctionTest {

  private RequestUriSpecFunction target;

  @SuppressWarnings("unchecked")
  private final Function<Invocation, HttpRequestMethod> httpMethodResolver = mock(Function.class);

  @BeforeEach
  void init() {
    target = Mockito.mock(RequestUriSpecFunction.class);
    when(target.apply(any(), any())).thenCallRealMethod();
    when(target.getHttpMethodResolver()).thenReturn(httpMethodResolver);
  }

  @Test
  void apply() {
    when(httpMethodResolver.apply(any())).thenReturn(HttpRequestMethod.GET);
    WebClient webClient = mock(WebClient.class);
    WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    //noinspection unchecked,rawtypes
    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
    WebClient.RequestHeadersUriSpec<?> actual = target.apply(mock(Invocation.class), webClient);
    assertThat(actual)
        .isEqualTo(requestHeadersUriSpec);
  }

  @Test
  void applyWithEmpty() throws Exception {
    when(httpMethodResolver.apply(any())).thenReturn(null);
    WebClient webClient = mock(WebClient.class);
    Method method = Example.class.getMethod("junit");
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> {
          Invocation invocation = mock(Invocation.class);
          when(invocation.getMethod()).thenReturn(method);
          target.apply(invocation, webClient);
        });
  }

  interface Example {

    void junit();
  }
}