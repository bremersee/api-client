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

package org.bremersee.apiclient.webflux.app;

import java.util.LinkedHashMap;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The value controller implementation.
 *
 * @author Christian Bremer
 */
@RestController
public class ValueControllerImpl implements ValueController {

  @Override
  public Publisher<String> getStringValue() {
    return Mono.just(STRING_VALUE);
  }

  @Override
  public Flux<Map<String, Object>> getJsonValues() {
    return Flux.fromStream(JSON_VALUES.stream());
  }

  @Override
  public Mono<String> putStringValue(String name, String payload) {
    return Mono.just(name + "=" + payload);
  }

  @Override
  public Mono<Void> patchStringValue(String name, String suffix, String payload) {
    if ("exception".equalsIgnoreCase(suffix)) {
      throw new IllegalArgumentException("'exception' is an illegal suffix");
    }
    return Mono.empty();
  }

  @Override
  public Mono<Map<String, Object>> postValue(
      String name,
      MultiValueMap<String, String> headers,
      String cookie,
      Map<String, Object> requestParams,
      Map<String, Object> payload) {

    Map<String, Object> map = new LinkedHashMap<>();
    map.put("PathVariable", name);
    map.put("x-custom", headers.getFirst("x-custom"));
    map.put("sweet", cookie);
    map.putAll(requestParams);
    map.putAll(payload);
    return Mono.just(map);
  }

  @Override
  public Mono<Boolean> deleteValue(String name) {
    return Mono.just(true);
  }

}
