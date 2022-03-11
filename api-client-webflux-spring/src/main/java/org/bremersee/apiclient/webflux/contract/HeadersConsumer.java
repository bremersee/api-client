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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

/**
 * The headers consumer.
 */
@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface HeadersConsumer extends BiConsumer<Invocation, HttpHeaders> {

  /**
   * Builder.
   *
   * @return the headers consumer builder
   */
  static ImmutableHeadersConsumer.Builder builder() {
    return ImmutableHeadersConsumer.builder();
  }

  /**
   * Gets content type resolver.
   *
   * @return the content type resolver
   */
  @NotNull
  Function<Invocation, Optional<MediaType>> getContentTypeResolver();

  /**
   * Gets accept resolver.
   *
   * @return the accept resolver
   */
  @NotNull
  Function<Invocation, MediaType> getAcceptResolver();

  /**
   * Gets headers resolver.
   *
   * @return the headers resolver
   */
  @NotNull
  Function<Invocation, MultiValueMap<String, String>> getHeadersResolver();

  @Override
  default void accept(Invocation invocation, HttpHeaders httpHeaders) {
    getContentTypeResolver().apply(invocation)
        .ifPresent(httpHeaders::setContentType);
    List<MediaType> accepts = new ArrayList<>();
    accepts.add(getAcceptResolver().apply(invocation));
    httpHeaders.setAccept(accepts);
    httpHeaders.addAll(getHeadersResolver().apply(invocation));
  }

}
