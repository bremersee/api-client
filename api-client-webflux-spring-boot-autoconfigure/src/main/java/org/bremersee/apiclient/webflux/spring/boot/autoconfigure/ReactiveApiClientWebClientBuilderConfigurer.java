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

import org.springframework.web.reactive.function.client.WebClient;

/**
 * The reactive api client web client builder configurer.
 *
 * @author Christian Bremer
 */
public interface ReactiveApiClientWebClientBuilderConfigurer {

  /**
   * Configure.
   *
   * @param webClientBuilder the web client builder
   */
  void configure(WebClient.Builder webClientBuilder);

}
