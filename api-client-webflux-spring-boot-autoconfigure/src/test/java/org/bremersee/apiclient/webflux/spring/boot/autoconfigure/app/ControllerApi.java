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

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The controller api.
 *
 * @author Christian Bremer
 */
@RequestMapping(path = "/api")
public interface ControllerApi {

  /**
   * Post data mono.
   *
   * @param data the data
   * @return the mono
   */
  @PostMapping(path = "/data", consumes = MediaType.ALL_VALUE)
  Mono<String> postData(@RequestBody Flux<DataBuffer> data);

  /**
   * Post parts mono.
   *
   * @param stringPart the string part
   * @return the mono
   */
  @PostMapping(
      path = "/multipart/parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<String> postParts(
      @RequestPart(name = "string") FormFieldPart stringPart);

  /**
   * Post publisher mono.
   *
   * @param publisher the publisher
   * @return the mono
   */
  @RequestMapping(path = "/publisher", method = RequestMethod.POST, consumes = "text/*")
  Mono<String> postPublisher(@RequestBody Publisher<String> publisher);

  /**
   * Post resource mono.
   *
   * @param resource the resource
   * @return the mono
   */
  @RequestMapping(path = "/resource", method = RequestMethod.POST, consumes = "text/*")
  Mono<String> postResource(@RequestBody Resource resource);

  /**
   * Put string value mono.
   *
   * @param name the name
   * @param payload the payload
   * @return the mono
   */
  @PutMapping(path = "/value/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.TEXT_PLAIN_VALUE)
  Mono<Map<String, Object>> putStringValue(
      @PathVariable("name") String name,
      @RequestBody String payload);

}
