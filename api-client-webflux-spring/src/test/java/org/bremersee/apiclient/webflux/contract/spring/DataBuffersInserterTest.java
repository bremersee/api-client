/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import reactor.core.publisher.Flux;

/**
 * The data buffers inserter test.
 *
 * @author Christian Bremer
 */
class DataBuffersInserterTest {

  private static final DataBuffersInserter target = new DataBuffersInserter();

  /**
   * Is possible body value.
   *
   * @throws Exception the exception
   */
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

  /**
   * Map body.
   *
   * @throws Exception the exception
   */
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

  /**
   * Insert.
   */
  @Test
  void insert() {
    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(requestBodyUriSpec.body(any(BodyInserter.class))).thenReturn(expected);
    //noinspection unchecked
    RequestHeadersUriSpec<?> actual = target.insert(mock(Flux.class), requestBodyUriSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Method a.
     *
     * @param data the data
     */
    void methodA(Flux<DataBuffer> data);
  }
}