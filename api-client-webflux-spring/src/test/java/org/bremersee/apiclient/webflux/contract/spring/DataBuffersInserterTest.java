package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import reactor.core.publisher.Flux;

class DataBuffersInserterTest {

  private static final DataBuffersInserter target = new DataBuffersInserter();

  @Test
  void isPossibleBodyValue() throws Exception {
    Method method = Example.class.getMethod("methodA", Flux.class);
    Flux<DataBuffer> value = Flux.empty();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation, method.getParameters()[0], value, 0);
    boolean actual = target.isPossibleBodyValue(invocationParameter);
    assertThat(actual)
        .isTrue();
  }

  @Test
  void mapBody() throws Exception {
    Method method = Example.class.getMethod("methodA", Flux.class);
    Flux<DataBuffer> value = Flux.empty();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation, method.getParameters()[0], value, 0);
    Publisher<DataBuffer> actual = target.mapBody(invocationParameter);
    assertThat(actual)
        .isEqualTo(value);
  }

  @Test
  void insert() {
    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    //noinspection unchecked
    when(requestBodyUriSpec.body(any())).thenReturn(mock(WebClient.RequestHeadersSpec.class));
    //noinspection unchecked
    RequestHeadersUriSpec<?> actual = target.insert(mock(Flux.class), requestBodyUriSpec);
    assertThat(actual)
        .isNotNull();
    verify(requestBodyUriSpec.body(any()));
  }

  interface Example {

    void methodA(Flux<DataBuffer> data);
  }
}