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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The multipart data inserter test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class MultipartDataInserterTest {

  private MultipartDataInserter target;

  private Function<Invocation, Optional<MediaType>> contentTypeResolver;

  private Converter<Part, HttpEntity<?>> partConverter;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    //noinspection unchecked
    contentTypeResolver = mock(Function.class);
    //noinspection unchecked
    partConverter = mock(Converter.class);
    target = new MultipartDataInserter()
        .withContentTypeResolver(contentTypeResolver)
        .withPartConverter(partConverter);
  }

  /**
   * Can insert.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
  @Test
  void canInsert(SoftAssertions softly) throws Exception {
    when(contentTypeResolver.apply(any())).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));

    Method method = Example.class.getMethod("goodA", MultiValueMap.class);
    Object value = new LinkedMultiValueMap<String, Part>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    softly.assertThat(target.canInsert(invocation))
        .isTrue();

    method = Example.class.getMethod("goodB", Mono.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Mono.just(new LinkedMultiValueMap<String, Part>());
    invocation = new Invocation(Example.class, method, new Object[]{value});
    softly.assertThat(target.canInsert(invocation))
        .isTrue();

    method = Example.class.getMethod("goodC", Flux.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Flux.fromStream(Stream.of(mock(Part.class)));
    invocation = new Invocation(Example.class, method, new Object[]{value});
    softly.assertThat(target.canInsert(invocation))
        .isTrue();

    method = Example.class.getMethod("goodD", Part.class);
    value = mock(Part.class);
    invocation = new Invocation(Example.class, method, new Object[]{value});
    softly.assertThat(target.canInsert(invocation))
        .isTrue();

    method = Example.class.getMethod("goodE", Mono.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Mono.just(mock(Part.class));
    invocation = new Invocation(Example.class, method, new Object[]{value});
    softly.assertThat(target.canInsert(invocation))
        .isTrue();

    method = Example.class.getMethod("goodF", Flux.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Flux.fromStream(Stream.of(mock(Part.class)));
    invocation = new Invocation(Example.class, method, new Object[]{value});
    softly.assertThat(target.canInsert(invocation))
        .isTrue();
  }

  /**
   * Apply.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
  @Test
  void apply(SoftAssertions softly) throws Exception {
    //noinspection unchecked
    when(partConverter.convert(any(Part.class))).thenReturn(mock(HttpEntity.class));

    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    //noinspection rawtypes
    RequestHeadersUriSpec expected = mock(RequestHeadersUriSpec.class);
    //noinspection unchecked
    when(requestBodyUriSpec.body(any())).thenReturn(expected);

    Method method = Example.class.getMethod("goodA", MultiValueMap.class);
    Object value = new LinkedMultiValueMap<String, Part>();
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    RequestHeadersUriSpec<?> actual = target.apply(invocation, requestBodyUriSpec);
    softly.assertThat(actual)
        .isEqualTo(expected);

    method = Example.class.getMethod("goodB", Mono.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Mono.just(new LinkedMultiValueMap<String, Part>());
    invocation = new Invocation(Example.class, method, new Object[]{value});
    actual = target.apply(invocation, requestBodyUriSpec);
    softly.assertThat(actual)
        .isEqualTo(expected);

    method = Example.class.getMethod("goodC", Flux.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Flux.fromStream(Stream.of(mock(Part.class)));
    invocation = new Invocation(Example.class, method, new Object[]{value});
    actual = target.apply(invocation, requestBodyUriSpec);
    softly.assertThat(actual)
        .isEqualTo(expected);

    method = Example.class.getMethod("goodD", Part.class);
    value = mock(Part.class);
    invocation = new Invocation(Example.class, method, new Object[]{value});
    actual = target.apply(invocation, requestBodyUriSpec);
    softly.assertThat(actual)
        .isEqualTo(expected);

    method = Example.class.getMethod("goodE", Mono.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Mono.just(mock(Part.class));
    invocation = new Invocation(Example.class, method, new Object[]{value});
    actual = target.apply(invocation, requestBodyUriSpec);
    softly.assertThat(actual)
        .isEqualTo(expected);

    method = Example.class.getMethod("goodF", Flux.class);
    //noinspection ReactiveStreamsUnusedPublisher
    value = Flux.fromStream(Stream.of(mock(Part.class)));
    invocation = new Invocation(Example.class, method, new Object[]{value});
    actual = target.apply(invocation, requestBodyUriSpec);
    softly.assertThat(actual)
        .isEqualTo(expected);
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Good a.
     *
     * @param body the body
     */
    void goodA(@RequestBody MultiValueMap<String, Part> body);

    /**
     * Good b.
     *
     * @param body the body
     */
    void goodB(@RequestBody Mono<MultiValueMap<String, Part>> body);

    /**
     * Good c.
     *
     * @param body the body
     */
    void goodC(@RequestBody Flux<Part> body);

    /**
     * Good d.
     *
     * @param part the part
     */
    void goodD(@RequestPart(name = "part") Part part);

    /**
     * Good e.
     *
     * @param part the part
     */
    void goodE(@RequestPart(name = "part") Mono<Part> part);

    /**
     * Good f.
     *
     * @param parts the parts
     */
    void goodF(@RequestPart(name = "part") Flux<Part> parts);

  }

}