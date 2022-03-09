package org.bremersee.apiclient.webflux.spring.boot.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.ReactiveErrorHandler;
import org.bremersee.exception.webclient.DefaultWebClientErrorDecoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass({ReactiveErrorHandler.class, DefaultWebClientErrorDecoder.class})
@Configuration
@Slf4j
public class ReactiveErrorHandlerAutoConfiguration {

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
  public ReactiveErrorHandler reactiveErrorHandler() {
    return ReactiveErrorHandler.builder()
        .errorFunction(new DefaultWebClientErrorDecoder())
        .build();
  }

}
