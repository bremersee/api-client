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

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

/**
 * The form data controller.
 *
 * @author Christian Bremer
 */
public interface FormDataController {

  /**
   * Post form data mono.
   *
   * @param headerValue the header value
   * @param lastValue the last value
   * @param data the data
   * @return the mono
   */
  @RequestMapping(path = "/upload",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postFormData(
      @RequestHeader(name = "x-ok-flag") String headerValue,
      @CookieValue(name = "last") String lastValue,
      @RequestBody MultiValueMap<String, String> data);

}
