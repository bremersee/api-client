/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.apiclient.webflux.spring;

import static java.util.Objects.nonNull;

import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bremersee.exception.webclient.DefaultWebClientErrorDecoder;
import org.bremersee.exception.webclient.WebClientErrorDecoder;
import org.springframework.http.HttpStatus;

/**
 * The invocation functions.
 *
 * @author Christian Bremer
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class InvocationFunctions {

  private RequestUriSpecBuilder uriSpecBuilder;

  private RequestUriBuilder uriBuilder;

  private RequestHeadersBuilder headersBuilder;

  private RequestCookiesBuilder cookiesBuilder;

  private RequestBodyInserter bodyInserter;

  private Predicate<HttpStatus> errorDetector;

  private WebClientErrorDecoder<? extends Throwable> errorDecoder;

  private RequestResponseBuilder responseBuilder;

  private InvocationFunctions() {
    this(RequestUriSpecBuilder.defaultBuilder(),
        RequestUriBuilder.defaultBuilder(),
        RequestHeadersBuilder.defaultBuilder(),
        RequestCookiesBuilder.defaultBuilder(),
        RequestBodyInserter.defaultInserter(),
        HttpStatus::isError,
        new DefaultWebClientErrorDecoder(),
        RequestResponseBuilder.defaultBuilder());
  }

  /**
   * Instantiates new invocation functions.
   *
   * @param uriSpecBuilder the uri spec builder
   * @param uriBuilder the uri builder
   * @param headersBuilder the headers builder
   * @param cookiesBuilder the cookies builder
   * @param bodyInserter the body inserter
   * @param errorDetector the error detector
   * @param errorDecoder the error decoder
   * @param responseBuilder the response builder
   */
  @Builder(toBuilder = true)
  public InvocationFunctions(
      final RequestUriSpecBuilder uriSpecBuilder,
      final RequestUriBuilder uriBuilder,
      final RequestHeadersBuilder headersBuilder,
      final RequestCookiesBuilder cookiesBuilder,
      final RequestBodyInserter bodyInserter,
      final Predicate<HttpStatus> errorDetector,
      final WebClientErrorDecoder<? extends Throwable> errorDecoder,
      final RequestResponseBuilder responseBuilder) {
    this.uriSpecBuilder = uriSpecBuilder;
    this.uriBuilder = uriBuilder;
    this.headersBuilder = headersBuilder;
    this.cookiesBuilder = cookiesBuilder;
    this.bodyInserter = bodyInserter;
    this.errorDetector = errorDetector;
    this.errorDecoder = errorDecoder;
    this.responseBuilder = responseBuilder;
  }

  /**
   * Merge invocation functions.
   *
   * @param commonFunctions the common functions
   * @param methodFunctions the method functions
   * @return the invocation functions
   */
  static InvocationFunctions merge(
      final InvocationFunctions commonFunctions,
      final InvocationFunctions methodFunctions) {

    final InvocationFunctions functions = new InvocationFunctions();
    mergeInto(commonFunctions, functions);
    mergeInto(methodFunctions, functions);
    return functions;
  }

  private static void mergeInto(
      final InvocationFunctions source,
      final InvocationFunctions destination) {

    if (nonNull(source)) {
      if (nonNull(source.getUriSpecBuilder())) {
        destination.setUriSpecBuilder(source.getUriSpecBuilder());
      }
      if (nonNull(source.getUriBuilder())) {
        destination.setUriBuilder(source.getUriBuilder());
      }
      if (nonNull(source.getHeadersBuilder())) {
        destination.setHeadersBuilder(source.getHeadersBuilder());
      }
      if (nonNull(source.getCookiesBuilder())) {
        destination.setCookiesBuilder(source.getCookiesBuilder());
      }
      if (nonNull(source.getBodyInserter())) {
        destination.setBodyInserter(source.getBodyInserter());
      }
      if (nonNull(source.getErrorDecoder())) {
        destination.setErrorDecoder(source.getErrorDecoder());
      }
      if (nonNull(source.getErrorDetector())) {
        destination.setErrorDetector(source.getErrorDetector());
      }
      if (nonNull(source.getResponseBuilder())) {
        destination.setResponseBuilder(source.getResponseBuilder());
      }
    }
  }

}
