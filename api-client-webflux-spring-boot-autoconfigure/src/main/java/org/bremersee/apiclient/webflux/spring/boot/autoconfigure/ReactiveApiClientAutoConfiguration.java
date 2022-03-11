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

package org.bremersee.apiclient.webflux.spring.boot.autoconfigure;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.ReactiveApiClient;
import org.bremersee.apiclient.webflux.ReactiveContract;
import org.bremersee.apiclient.webflux.ReactiveErrorHandler;
import org.bremersee.apiclient.webflux.contract.HeadersConsumer;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserter;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserterRegistry;
import org.bremersee.apiclient.webflux.contract.RequestUriFunction;
import org.bremersee.apiclient.webflux.contract.spring.AcceptResolver;
import org.bremersee.apiclient.webflux.contract.spring.ContentTypeResolver;
import org.bremersee.apiclient.webflux.contract.spring.DataBuffersInserter;
import org.bremersee.apiclient.webflux.contract.spring.FormDataInserter;
import org.bremersee.apiclient.webflux.contract.spring.MultipartDataInserter;
import org.bremersee.apiclient.webflux.contract.spring.PageableRequestParameterResolver;
import org.bremersee.apiclient.webflux.contract.spring.PartToHttpEntityConverter;
import org.bremersee.apiclient.webflux.contract.spring.PathVariablesResolver;
import org.bremersee.apiclient.webflux.contract.spring.PublisherInserter;
import org.bremersee.apiclient.webflux.contract.spring.QueryParametersResolver;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.bremersee.apiclient.webflux.contract.spring.RequestHeadersResolver;
import org.bremersee.apiclient.webflux.contract.spring.RequestParametersResolver;
import org.bremersee.apiclient.webflux.contract.spring.RequestPathResolver;
import org.bremersee.apiclient.webflux.contract.spring.ResourceInserter;
import org.bremersee.apiclient.webflux.contract.spring.SortRequestParameterResolver;
import org.bremersee.apiclient.webflux.contract.spring.ValueInserter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The reactive api client autoconfiguration.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass({ReactiveApiClient.class, ReactiveSpringContract.class})
@Configuration
@Slf4j
public class ReactiveApiClientAutoConfiguration {

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("\n"
            + "*********************************************************************************\n"
            + "* {}\n"
            + "*********************************************************************************",
        ClassUtils.getUserClass(getClass()).getSimpleName());
  }

  /**
   * Content type resolver.
   *
   * @return the content type resolver
   */
  @ConditionalOnMissingBean
  @Bean
  public ContentTypeResolver contentTypeResolver() {
    return new ContentTypeResolver();
  }

  /**
   * Request parameters resolver.
   *
   * @return the request parameters resolver
   */
  @ConditionalOnMissingBean
  @Bean
  @Order(-1000)
  public RequestParametersResolver requestParametersResolver() {
    log.info("Creating {} with order {}", RequestParametersResolver.class.getSimpleName(), -1000);
    return new RequestParametersResolver();
  }

  /**
   * Sort request parameter resolver.
   *
   * @return the sort request parameter resolver
   */
  @ConditionalOnClass(name = "org.springframework.data.domain.Sort")
  @ConditionalOnMissingBean
  @Bean
  @Order(-500)
  public SortRequestParameterResolver sortRequestParameterResolver() {
    log.info("Creating {} with order {}", SortRequestParameterResolver.class.getSimpleName(), -500);
    return new SortRequestParameterResolver();
  }

  /**
   * Pageable request parameter resolver.
   *
   * @return the pageable request parameter resolver
   */
  @ConditionalOnClass(name = "org.springframework.data.domain.Pageable")
  @ConditionalOnMissingBean
  @Bean
  @Order(-510)
  public PageableRequestParameterResolver pageableRequestParameterResolver() {
    log.info(
        "Creating {} with order {}",
        PageableRequestParameterResolver.class.getSimpleName(),
        -510);
    return new PageableRequestParameterResolver();
  }

  /**
   * Form data inserter.
   *
   * @param contentTypeResolver the content type resolver
   * @return the request body inserter
   */
  @Bean
  @Order(100)
  public RequestBodyInserter formDataInserter(ContentTypeResolver contentTypeResolver) {
    log.info("Creating {} with order {}", FormDataInserter.class.getSimpleName(), 100);
    return new FormDataInserter()
        .withContentTypeResolver(contentTypeResolver);
  }

  /**
   * Multipart data inserter.
   *
   * @param contentTypeResolver the content type resolver
   * @param partConverter the part converter
   * @return the request body inserter
   */
  @Bean
  @Order(200)
  public RequestBodyInserter multipartDataInserter(
      ContentTypeResolver contentTypeResolver,
      ObjectProvider<Converter<Part, HttpEntity<?>>> partConverter) {

    log.info("Creating {} with order {}", MultipartDataInserter.class.getSimpleName(), 200);
    return new MultipartDataInserter()
        .withContentTypeResolver(contentTypeResolver)
        .withPartConverter(partConverter.getIfAvailable(PartToHttpEntityConverter::new));
  }

  /**
   * Resource inserter.
   *
   * @return the request body inserter
   */
  @Bean
  @Order(300)
  public RequestBodyInserter resourceInserter() {
    log.info("Creating {} with order {}", ResourceInserter.class.getSimpleName(), 300);
    return new ResourceInserter();
  }

  /**
   * Data buffers inserter.
   *
   * @return the request body inserter
   */
  @Bean
  @Order(400)
  public RequestBodyInserter dataBuffersInserter() {
    log.info("Creating {} with order {}", DataBuffersInserter.class.getSimpleName(), 400);
    return new DataBuffersInserter();
  }

  /**
   * Publisher inserter.
   *
   * @return the request body inserter
   */
  @Bean
  @Order(500)
  public RequestBodyInserter publisherInserter() {
    log.info("Creating {} with order {}", PublisherInserter.class.getSimpleName(), 500);
    return new PublisherInserter();
  }

  /**
   * Value inserter.
   *
   * @return the request body inserter
   */
  @Bean
  @Order(600)
  public RequestBodyInserter valueInserter() {
    log.info("Creating {} with order {}", ValueInserter.class.getSimpleName(), 600);
    return new ValueInserter();
  }

  /**
   * Request body inserter registry.
   *
   * @param requestBodyInserters the request body inserters
   * @return the request body inserter registry
   */
  @ConditionalOnMissingBean
  @Bean
  public RequestBodyInserterRegistry requestBodyInserterRegistry(
      ObjectProvider<RequestBodyInserter> requestBodyInserters) {

    List<RequestBodyInserter> requestBodyInserterList = requestBodyInserters
        .orderedStream()
        .collect(Collectors.toList());
    log.info(
        "Creating {} with inserters {}",
        RequestBodyInserterRegistry.class.getSimpleName(),
        requestBodyInserterList);
    return RequestBodyInserterRegistry.builder()
        .requestBodyInserters(requestBodyInserterList)
        .build();
  }

  /**
   * Reactive spring contract.
   *
   * @param contentTypeResolver the content type resolver
   * @param queryParametersResolvers the query parameters resolvers
   * @param requestBodyInserterRegistry the request body inserter registry
   * @return the reactive contract
   */
  @ConditionalOnMissingBean
  @Bean
  public ReactiveContract reactiveSpringContract(
      ContentTypeResolver contentTypeResolver,
      ObjectProvider<QueryParametersResolver> queryParametersResolvers,
      RequestBodyInserterRegistry requestBodyInserterRegistry) {

    List<Function<Invocation, MultiValueMap<String, Object>>> queryParametersResolverList
        = queryParametersResolvers.orderedStream().collect(Collectors.toList());
    log.info(
        "Creating {} with queryParametersResolvers {}",
        ReactiveContract.class.getSimpleName(),
        queryParametersResolverList);
    ReactiveSpringContract reactiveSpringContract = new ReactiveSpringContract();
    return ReactiveContract.builder()
        .from(reactiveSpringContract)
        .headersConsumer(HeadersConsumer.builder()
            .contentTypeResolver(contentTypeResolver)
            .acceptResolver(new AcceptResolver())
            .headersResolver(new RequestHeadersResolver())
            .build())
        .requestUriFunction(RequestUriFunction.builder()
            .requestPathResolver(new RequestPathResolver())
            .pathVariablesResolver(new PathVariablesResolver())
            .requestParametersResolvers(queryParametersResolverList)
            .build())
        .requestBodyInserterFunction(requestBodyInserterRegistry)
        .build();
  }

  /**
   * Reactive api client.
   *
   * @param configurers the configurers
   * @param reactiveContract the reactive contract
   * @param errorHandler the error handler
   * @return the reactive api client
   */
  @ConditionalOnMissingBean
  @Bean
  public ReactiveApiClient reactiveApiClient(
      ObjectProvider<ReactiveApiClientWebClientBuilderConfigurer> configurers,
      ReactiveContract reactiveContract,
      ObjectProvider<ReactiveErrorHandler> errorHandler) {

    WebClient.Builder webClientBuilder = WebClient.builder();
    configurers.orderedStream().forEach(configurer -> configurer.configure(webClientBuilder));
    return new ReactiveApiClient(webClientBuilder, reactiveContract, errorHandler.getIfAvailable());
  }

}
