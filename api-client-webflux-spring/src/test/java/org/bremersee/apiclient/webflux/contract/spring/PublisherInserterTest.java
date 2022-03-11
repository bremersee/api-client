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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import reactor.core.publisher.Mono;

class PublisherInserterTest {

  private static final PublisherInserter target = new PublisherInserter();

  @Test
  void isPossibleBodyValue() {
    InvocationParameter invocationParameter = mock(InvocationParameter.class);
    when(invocationParameter.getValue()).thenReturn(Mono.empty());
    assertThat(target.isPossibleBodyValue(invocationParameter))
        .isTrue();
  }

  @Test
  void mapBody() {
    InvocationParameter expected = mock(InvocationParameter.class);
    assertThat(target.mapBody(expected))
        .isEqualTo(expected);
  }

  @Test
  void insert() throws Exception {
    Method method = Example.class.getMethod("methodA", Publisher.class);
    //noinspection
    Object value = Mono.just("123");
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation, method.getParameters()[0], value, 0);

    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(requestBodyUriSpec.body(any(Object.class), any(ParameterizedTypeReference.class)))
        .thenReturn(expected);

    RequestHeadersUriSpec<?> actual = target.insert(invocationParameter, requestBodyUriSpec);
    assertThat(actual)
        .isEqualTo(expected);

    verify(requestBodyUriSpec).body(any(Object.class), any(ParameterizedTypeReference.class));
  }

  interface Example {

    void methodA(@RequestBody Publisher<String> body);
  }
}