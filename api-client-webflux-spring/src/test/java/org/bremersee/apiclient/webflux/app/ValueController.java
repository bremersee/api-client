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

package org.bremersee.apiclient.webflux.app;

import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The value controller.
 *
 * @author Christian Bremer
 */
public interface ValueController {

  String STRING_VALUE = "OK";

  List<Map<String, Object>> JSON_VALUES = List.of(
      Map.of("key0", "value0"),
      Map.of("key1", "value1"),
      Map.of("key2", "value2")
  );

  /**
   * Simple get mono.
   *
   * @return the mono
   */
  @GetMapping
  Publisher<String> getStringValue();

  /**
   * Gets oks.
   *
   * @return the oks
   */
  @GetMapping(path = "/api/value", produces = MediaType.APPLICATION_JSON_VALUE)
  Flux<Map<String, Object>> getJsonValues();

  /**
   * Update ok mono.
   *
   * @param name the name
   * @param payload the payload
   * @return the mono
   */
  @PutMapping(path = "/api/value/{name}",
      produces = MediaType.TEXT_PLAIN_VALUE,
      consumes = MediaType.TEXT_PLAIN_VALUE)
  Mono<String> putStringValue(
      @PathVariable("name") String name,
      @RequestBody String payload);

  /**
   * Patch ok mono.
   *
   * @param name the name
   * @param suffix the suffix
   * @param payload the payload
   * @return the mono
   */
  @PatchMapping(path = "/api/value/{name}",
      consumes = MediaType.TEXT_PLAIN_VALUE)
  Mono<Void> patchStringValue(
      @PathVariable("name") String name,
      @RequestParam(name = "suffix") String suffix,
      @RequestBody String payload);

  @PostMapping(path = "/api/value/{name}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postValue(
      @PathVariable("name") String name,
      @RequestHeader MultiValueMap<String, String> headers,
      @CookieValue(name = "sweet") String cookie,
      @RequestParam Map<String, Object> requestParams,
      @RequestBody Map<String, Object> payload);

  /**
   * Delete ok mono.
   *
   * @param name the name
   * @return the mono
   */
  @DeleteMapping(path = "/api/value/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Boolean> deleteValue(@PathVariable("name") String name);

}
