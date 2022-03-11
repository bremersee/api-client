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

package org.bremersee.apiclient.webflux.contract.spring;

import static org.bremersee.apiclient.webflux.Invocation.findAnnotationValue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class AcceptResolver implements Function<Invocation, MediaType> {

  @Override
  public MediaType apply(Invocation invocation) {
    Method method = invocation.getMethod();
    return Arrays.stream(
            findAnnotationValue(method, RequestMapping.class, a -> a.produces().length > 0, RequestMapping::produces)
                .or(() -> findAnnotationValue(
                    method, GetMapping.class, a -> a.produces().length > 0, GetMapping::produces))
                .or(() -> findAnnotationValue(
                    method, PostMapping.class, a -> a.produces().length > 0, PostMapping::produces))
                .or(() -> findAnnotationValue(
                    method, PutMapping.class, a -> a.produces().length > 0, PutMapping::produces))
                .or(() -> findAnnotationValue(
                    method, PatchMapping.class, a -> a.produces().length > 0, PatchMapping::produces))
                .or(() -> findAnnotationValue(
                    method, DeleteMapping.class, a -> a.produces().length > 0, DeleteMapping::produces))
                .orElse(new String[0]))
        .map(this::parseMediaType)
        .findFirst()
        .orElse(MediaType.ALL);
  }

  protected MediaType parseMediaType(String mediaType) {
    try {
      return MediaType.parseMediaType(mediaType);
    } catch (RuntimeException ignored) {
      return null;
    }
  }
}
