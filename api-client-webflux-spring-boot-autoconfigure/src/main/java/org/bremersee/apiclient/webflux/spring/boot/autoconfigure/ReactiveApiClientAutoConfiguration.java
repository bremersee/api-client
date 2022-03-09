package org.bremersee.apiclient.webflux.spring.boot.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.ReactiveApiClient;
import org.bremersee.apiclient.webflux.ReactiveContract;
import org.bremersee.apiclient.webflux.ReactiveErrorHandler;
import org.bremersee.apiclient.webflux.contract.spring.ReactiveSpringContract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;
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
  public ReactiveContract reactiveSpringContract() {
    return new ReactiveSpringContract();
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
