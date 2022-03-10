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

class FormDataInserterTest {

  private FormDataInserter target;

  private Function<Invocation, Optional<MediaType>> contentTypeResolver;

  @BeforeEach
  void init() {
    //noinspection unchecked
    contentTypeResolver = mock(Function.class);
    target = new FormDataInserter()
        .withContentTypeResolver(contentTypeResolver);
  }

  @Test
  void canInsert() throws Exception {
    Method method = Example.class.getMethod("methodA", MultiValueMap.class);
    MultiValueMap<String, String> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    when(contentTypeResolver.apply(eq(invocation))).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));
    assertThat(target.canInsert(invocation))
        .isTrue();
  }

  @Test
  void canInsertNotOtherMediaTypes() throws Exception {
    Method method = Example.class.getMethod("methodA", MultiValueMap.class);
    MultiValueMap<String, String> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    when(contentTypeResolver.apply(eq(invocation))).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
    assertThat(target.canInsert(invocation))
        .isFalse();
  }

  @Test
  void canInsertNotAnyMultiValueMap() throws Exception {
    Method method = Example.class.getMethod("methodB", MultiValueMap.class);
    MultiValueMap<Object, Object> value = new LinkedMultiValueMap<>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    when(contentTypeResolver.apply(eq(invocation))).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
    assertThat(target.canInsert(invocation))
        .isFalse();
  }

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

  interface Example {

    void methodA(@RequestBody MultiValueMap<String, String> formData);

    void methodB(@RequestBody MultiValueMap<Object, Object> formData);
  }
}