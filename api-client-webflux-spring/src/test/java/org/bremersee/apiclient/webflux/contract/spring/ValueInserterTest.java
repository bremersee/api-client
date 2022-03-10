package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

class ValueInserterTest {

  private static final ValueInserter target = new ValueInserter();

  @Test
  void isPossibleBodyValue() {
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn("123");
    assertThat(target.isPossibleBodyValue(invocationParameter))
        .isTrue();
  }

  @Test
  void mapBody() {
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn("123");
    assertThat(target.mapBody(invocationParameter))
        .isEqualTo("123");
  }

  @Test
  void insert() throws Exception {
    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(requestBodyUriSpec.body(any(BodyInserter.class)))
        .thenReturn(expected);

    RequestHeadersUriSpec<?> actual = target.insert("123", requestBodyUriSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }
}