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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.ReactiveContract;
import org.bremersee.apiclient.webflux.contract.CookiesConsumer;
import org.bremersee.apiclient.webflux.contract.HeadersConsumer;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserterRegistry;
import org.bremersee.apiclient.webflux.contract.RequestUriFunction;
import org.bremersee.apiclient.webflux.contract.RequestUriSpecFunction;
import org.bremersee.apiclient.webflux.contract.ResponseFunction;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

/**
 * The reactive spring contract.
 *
 * @author Christian Bremer
 */
public class ReactiveSpringContract implements ReactiveContract {

  private final ContentTypeResolver contentTypeResolver = new ContentTypeResolver();

  @Override
  public BiConsumer<Invocation, MultiValueMap<String, String>> getCookiesConsumer() {
    return CookiesConsumer.builder()
        .cookiesResolver(new CookiesResolver())
        .build();
  }

  @Override
  public BiConsumer<Invocation, HttpHeaders> getHeadersConsumer() {
    return HeadersConsumer.builder()
        .contentTypeResolver(contentTypeResolver)
        .acceptResolver(new AcceptResolver())
        .headersResolver(new RequestHeadersResolver())
        .build();
  }

  @Override
  public BiFunction<Invocation, UriBuilder, URI> getRequestUriFunction() {
    return RequestUriFunction.builder()
        .requestPathResolver(new RequestPathResolver())
        .pathVariablesResolver(new PathVariablesResolver())
        .requestParametersResolvers(getRequestParametersResolvers())
        .build();
  }

  /**
   * Gets request parameters resolvers.
   *
   * @return the request parameters resolvers
   */
  protected List<Function<Invocation, MultiValueMap<String, Object>>>
  getRequestParametersResolvers() {

    List<Function<Invocation, MultiValueMap<String, Object>>> list = new ArrayList<>();
    list.add(new RequestParametersResolver());
    if (Extensions.isSortPresent) {
      list.add(new SortRequestParameterResolver());
    }
    if (Extensions.isPageablePresent) {
      list.add(new PageableRequestParameterResolver());
    }
    return list;
  }

  @Override
  public BiFunction<Invocation, WebClient, RequestHeadersUriSpec<?>> getRequestUriSpecFunction() {
    return RequestUriSpecFunction.builder()
        .httpMethodResolver(new HttpMethodResolver())
        .build();
  }

  @Override
  public BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>>
  getRequestBodyInserterFunction() {

    return RequestBodyInserterRegistry.builder()
        .addRequestBodyInserters(new FormDataInserter()
            .withContentTypeResolver(contentTypeResolver))
        .addRequestBodyInserters(new MultipartDataInserter()
            .withContentTypeResolver(contentTypeResolver))
        .addRequestBodyInserters(new ResourceInserter())
        .addRequestBodyInserters(new DataBuffersInserter())
        .addRequestBodyInserters(new PublisherInserter())
        .addRequestBodyInserters(new ValueInserter())
        .build();
  }

  @Override
  public BiFunction<Invocation, ResponseSpec, Publisher<?>> getResponseFunction() {
    return new ResponseFunction();
  }
}
