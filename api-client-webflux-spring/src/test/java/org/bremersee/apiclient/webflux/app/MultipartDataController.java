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
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MultipartDataController {

  @PostMapping(
      path = "/api/multipart/map",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postMultipartDataMap(@RequestBody MultiValueMap<String, Part> data);

  @PostMapping(
      path = "/api/multipart/mono-map",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postMonoMultipartDataMap(@RequestBody Mono<MultiValueMap<String, Part>> monoData);

  @PostMapping(
      path = "/api/multipart/parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postParts(
      @RequestPart(name = "string") Part stringPart,
      @RequestPart(name = "resource") Part resourcePart,
      @RequestPart(name = "buf", required = false) Part dataBufferPart,
      @RequestPart(name = "files", required = false) Part filePart);

  @PostMapping(
      path = "/api/multipart/mono-parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postMonoParts(
      @RequestPart(name = "string") Mono<Part> stringPart,
      @RequestPart(name = "resource") Mono<Part> resourcePart,
      @RequestPart(name = "buf", required = false) Mono<Part> dataBufferPart,
      @RequestPart(name = "files", required = false) Mono<Part> filePart);

  @PostMapping(
      path = "/api/multipart/flux-parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postFluxParts(
      @RequestBody Flux<Part> parts);

  @PostMapping(
      path = "/api/multipart/named-flux-parts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<Map<String, Object>> postNamedFluxParts(
      @RequestPart(name = "parts") Flux<Part> parts);

}
