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

package org.bremersee.apiclient.webflux.spring.boot.autoconfigure.app;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.apiclient.webflux.spring.boot.autoconfigure.ReactiveApiClientWebClientBuilderConfigurer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * The test configuration.
 *
 * @author Christian Bremer
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {TestConfiguration.class})
@Slf4j
public class TestConfiguration {

  /**
   * Web client configurer.
   *
   * @return the web client builder configurer
   */
  @Bean
  public ReactiveApiClientWebClientBuilderConfigurer webClientConfigurer() {
    return webClientBuilder -> webClientBuilder.filter((request, next) -> {
      log.info("Calling url {}", request.url());
      return next.exchange(request);
    });
  }

}
