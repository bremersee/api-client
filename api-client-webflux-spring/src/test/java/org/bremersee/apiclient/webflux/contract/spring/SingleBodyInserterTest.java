package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

class SingleBodyInserterTest {

  @Test
  void apply() {
    //noinspection unchecked
    SingleBodyInserter<String> target = mock(SingleBodyInserter.class);
    when(target.apply(any(), any())).thenCallRealMethod();
    String value = "123";
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn(value);
    when(target.findPossibleBodies(any())).thenReturn(List.of(invocationParameter));
    when(target.mapBody(any())).thenReturn(value);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(target.insert(anyString(), any())).thenReturn(expected);
    RequestHeadersUriSpec<?> actual = target.apply(
        mock(Invocation.class),
        mock(RequestBodyUriSpec.class));
    assertThat(actual)
        .isEqualTo(expected);
  }
}