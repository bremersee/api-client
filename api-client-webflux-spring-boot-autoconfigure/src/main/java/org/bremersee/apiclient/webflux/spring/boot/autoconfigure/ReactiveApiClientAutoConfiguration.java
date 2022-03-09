package org.bremersee.apiclient.webflux.spring.boot.autoconfigure;

import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.ReactiveApiClient;
import org.bremersee.apiclient.webflux.ReactiveContract;
import org.bremersee.apiclient.webflux.ReactiveErrorHandler;
import org.bremersee.apiclient.webflux.contract.HeadersConsumer;
import org.bremersee.apiclient.webflux.contract.RequestUriFunction;
import org.bremersee.apiclient.webflux.contract.spring.AcceptResolver;
import org.bremersee.apiclient.webflux.contract.spring.ContentTypeResolver;
import org.bremersee.apiclient.webflux.contract.spring.DataBuffersInserter;
import org.bremersee.apiclient.webflux.contract.spring.FormDataInserter;
import org.bremersee.apiclient.webflux.contract.spring.MultipartDataInserter;
import org.bremersee.apiclient.webflux.contract.spring.PageableRequestParameterResolver;
import org.bremersee.apiclient.webflux.contract.spring.PathVariablesResolver;
import org.bremersee.apiclient.webflux.contract.spring.PublisherInserter;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.bremersee.apiclient.webflux.contract.spring.RequestBodyInserter;
import org.bremersee.apiclient.webflux.contract.spring.RequestBodyInserterRegistry;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

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

  @ConditionalOnMissingBean
  @Bean
  public ContentTypeResolver contentTypeResolver() {
    return new ContentTypeResolver();
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(10)
  public Function<Invocation, MultiValueMap<String, Object>> requestParametersResolver() {
    return new RequestParametersResolver();
  }

  @ConditionalOnClass(name = "org.springframework.data.domain.Sort")
  @ConditionalOnMissingBean
  @Bean
  @Order(20)
  public Function<Invocation, MultiValueMap<String, Object>> sortRequestParameterResolver() {
    return new SortRequestParameterResolver();
  }

  @ConditionalOnClass(name = "org.springframework.data.domain.Pageable")
  @ConditionalOnMissingBean
  @Bean
  @Order(30)
  public Function<Invocation, MultiValueMap<String, Object>> pageableRequestParameterResolver() {
    return new PageableRequestParameterResolver();
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(10)
  public RequestBodyInserter formDataInserter(ContentTypeResolver contentTypeResolver) {
    return new FormDataInserter()
        .withContentTypeResolver(contentTypeResolver);
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(20)
  public RequestBodyInserter multipartDataInserter(ContentTypeResolver contentTypeResolver) {
    return new MultipartDataInserter()
        .withContentTypeResolver(contentTypeResolver);
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(30)
  public RequestBodyInserter resourceInserter() {
    return new ResourceInserter();
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(40)
  public RequestBodyInserter dataBuffersInserter() {
    return new DataBuffersInserter();
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(90)
  public RequestBodyInserter publisherInserter() {
    return new PublisherInserter();
  }

  @ConditionalOnMissingBean
  @Bean
  @Order(100)
  public RequestBodyInserter valueInserter() {
    return new ValueInserter();
  }

  @ConditionalOnMissingBean
  @Bean
  public RequestBodyInserterRegistry requestBodyInserterRegistry(
      ObjectProvider<RequestBodyInserter> requestBodyInserters) {

    return RequestBodyInserterRegistry.builder()
        .requestBodyInserters(requestBodyInserters.orderedStream().collect(Collectors.toList()))
        .build();
  }

  @ConditionalOnMissingBean
  @Bean
  public ReactiveContract reactiveSpringContract(
      ContentTypeResolver contentTypeResolver,
      ObjectProvider<Function<Invocation, MultiValueMap<String, Object>>> requestParametersResolvers,
      RequestBodyInserterRegistry requestBodyInserterRegistry) {

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
            .requestParametersResolvers(requestParametersResolvers.orderedStream().collect(Collectors.toList()))
            .build())
        .requestBodyInserterFunction(requestBodyInserterRegistry)
        .build();
  }

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
