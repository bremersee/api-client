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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * The form data controller implementation.
 *
 * <p>In a WebFlux application, form data is accessed via {@code ServerWebExchange.getFormData()}.
 * For this reason the controller can't implement the interface, because it's signature differs from
 * this implementation.
 *
 * @author Christian Bremer
 */
@RestController
public class FormDataControllerImpl { // Can't implement FormDataController

  /**
   * Post form data mono.
   *
   * @param headerValue the header value
   * @param lastValue the last value
   * @param exchange the exchange
   * @return the mono
   */
  @RequestMapping(path = "/upload",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map<String, Object>> postFormData(
      @RequestHeader(name = "x-ok-flag") String headerValue,
      @CookieValue(name = "last") String lastValue,
      ServerWebExchange exchange) {

    return exchange.getFormData().map(data -> {
      LinkedHashMap<String, Object> map = new LinkedHashMap<>();
      map.put("x-ok-flag", headerValue);
      map.put("last", lastValue);
      map.putAll(data);
      return map;
    });
  }
}
