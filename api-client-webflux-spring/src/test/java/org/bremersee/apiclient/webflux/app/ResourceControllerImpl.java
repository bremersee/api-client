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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * The resource controller implementation.
 *
 * @author Christian Bremer
 */
@RestController
public class ResourceControllerImpl implements ResourceController {

  @Override
  public Mono<String> postResource(Resource resource) {
    try {
      return Mono.just(new String(
          FileCopyUtils.copyToByteArray(resource.getInputStream()),
          StandardCharsets.UTF_8));

    } catch (IOException exception) {
      throw new IoRuntimeException("Creating string from resource failed", exception);
    }
  }
}
