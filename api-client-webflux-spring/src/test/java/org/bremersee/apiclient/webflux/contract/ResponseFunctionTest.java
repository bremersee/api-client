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

package org.bremersee.apiclient.webflux.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ResponseFunctionTest {

  private static final ResponseFunction target = new ResponseFunction();

  @Test
  void applyMono() throws Exception {
    ResponseSpec responseSpec = mock(ResponseSpec.class);
    //noinspection unchecked
    Mono<String> expected = mock(Mono.class);
    //noinspection unchecked
    when(responseSpec.bodyToMono(any(Class.class))).thenReturn(expected);

    Method method = Example.class.getMethod("getMono");
    Invocation invocation = new Invocation(Example.class, method, null);
    Publisher<?> actual = target.apply(invocation, responseSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void applyIntegerMono() throws Exception {
    ResponseSpec responseSpec = mock(ResponseSpec.class);
    IntegerMono expected = mock(IntegerMono.class);
    //noinspection unchecked
    when(responseSpec.bodyToMono(any(Class.class))).thenReturn(expected);

    Method method = Example.class.getMethod("getIntegerMono");
    Invocation invocation = new Invocation(Example.class, method, null);
    Publisher<?> actual = target.apply(invocation, responseSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void applyFlux() throws Exception {
    ResponseSpec responseSpec = mock(ResponseSpec.class);
    //noinspection unchecked
    Flux<String> expected = mock(Flux.class);
    //noinspection unchecked
    when(responseSpec.bodyToFlux(any(Class.class))).thenReturn(expected);

    Method method = Example.class.getMethod("getFlux");
    Invocation invocation = new Invocation(Example.class, method, null);
    Publisher<?> actual = target.apply(invocation, responseSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void applyIntegerFlux() throws Exception {
    ResponseSpec responseSpec = mock(ResponseSpec.class);
    IntegerFlux expected = mock(IntegerFlux.class);
    //noinspection unchecked
    when(responseSpec.bodyToFlux(any(Class.class))).thenReturn(expected);

    Method method = Example.class.getMethod("getIntegerFlux");
    Invocation invocation = new Invocation(Example.class, method, null);
    Publisher<?> actual = target.apply(invocation, responseSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void applyPublisher() throws Exception {
    ResponseSpec responseSpec = mock(ResponseSpec.class);
    IntegerFlux expected = mock(IntegerFlux.class);
    //noinspection unchecked
    when(responseSpec.bodyToFlux(any(Class.class))).thenReturn(expected);

    Method method = Example.class.getMethod("getPublisher");
    Invocation invocation = new Invocation(Example.class, method, null);
    Publisher<?> actual = target.apply(invocation, responseSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void applyIntegerPublisher() throws Exception {
    ResponseSpec responseSpec = mock(ResponseSpec.class);
    IntegerFlux expected = mock(IntegerFlux.class);
    //noinspection unchecked
    when(responseSpec.bodyToFlux(any(Class.class))).thenReturn(expected);

    Method method = Example.class.getMethod("getIntegerPublisher");
    Invocation invocation = new Invocation(Example.class, method, null);
    Publisher<?> actual = target.apply(invocation, responseSpec);
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void illegal() throws Exception {
    Method method = Example.class.getMethod("illegal");
    Invocation invocation = new Invocation(Example.class, method, null);
    //noinspection ReactiveStreamsUnusedPublisher
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> target.apply(invocation, mock(ResponseSpec.class)));
  }

  abstract static class IntegerMono extends Mono<Integer> {

  }

  abstract static class IntegerFlux extends Flux<Integer> {

  }

  @SuppressWarnings("ReactiveStreamsPublisherImplementation")
  abstract static class IntegerPublisher implements Publisher<Integer> {

  }

  interface Example {

    Mono<String> getMono();

    IntegerMono getIntegerMono();

    Flux<String> getFlux();

    IntegerFlux getIntegerFlux();

    Publisher<String> getPublisher();

    IntegerPublisher getIntegerPublisher();

    String illegal();
  }
}