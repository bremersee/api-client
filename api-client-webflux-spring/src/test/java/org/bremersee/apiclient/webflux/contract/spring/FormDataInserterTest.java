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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

/**
 * The form data inserter test.
 */
class FormDataInserterTest {

  private FormDataInserter target;

  private Function<Invocation, Optional<MediaType>> contentTypeResolver;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    //noinspection unchecked
    contentTypeResolver = mock(Function.class);
    target = new FormDataInserter()
        .withContentTypeResolver(contentTypeResolver);
  }

  /**
   * Can insert.
   *
   * @throws Exception the exception
   */
  @Test
  void canInsert() throws Exception {
    Method method = Example.class.getMethod("methodA", MultiValueMap.class);
    MultiValueMap<String, String> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    when(contentTypeResolver.apply(eq(invocation))).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));
    assertThat(target.canInsert(invocation))
        .isTrue();
  }

  /**
   * Can insert not other media types.
   *
   * @throws Exception the exception
   */
  @Test
  void canInsertNotOtherMediaTypes() throws Exception {
    Method method = Example.class.getMethod("methodA", MultiValueMap.class);
    MultiValueMap<String, String> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    when(contentTypeResolver.apply(eq(invocation))).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
    assertThat(target.canInsert(invocation))
        .isFalse();
  }

  /**
   * Can insert not any multi value map.
   *
   * @throws Exception the exception
   */
  @Test
  void canInsertNotAnyMultiValueMap() throws Exception {
    Method method = Example.class.getMethod("methodB", MultiValueMap.class);
    MultiValueMap<Object, Object> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    when(contentTypeResolver.apply(eq(invocation))).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
    assertThat(target.canInsert(invocation))
        .isFalse();
  }

  /**
   * Map body.
   *
   * @throws Exception the exception
   */
  @Test
  void mapBody() throws Exception {
    Method method = Example.class.getMethod("methodA", MultiValueMap.class);
    MultiValueMap<String, String> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation, method.getParameters()[0], value, 0);
    assertThat(target.mapBody(invocationParameter))
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

    MultiValueMap<String, String> value = new LinkedMultiValueMap<>();
    assertThat(target.insert(value, requestBodyUriSpec))
        .isEqualTo(expected);
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Method a.
     *
     * @param formData the form data
     */
    void methodA(@RequestBody MultiValueMap<String, String> formData);

    /**
     * Method b.
     *
     * @param formData the form data
     */
    void methodB(@RequestBody MultiValueMap<Object, Object> formData);
  }
}