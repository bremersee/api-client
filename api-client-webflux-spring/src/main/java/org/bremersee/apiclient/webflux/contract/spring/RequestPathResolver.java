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

import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The request path resolver.
 *
 * @author Christian Bremer
 */
public class RequestPathResolver implements Function<Invocation, String> {

  @Override
  public String apply(Invocation invocation) {
    // Request mapping on class
    String clsPath = invocation
        .findAnnotationValueOnTargetClass(
            RequestMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");

    // Request mapping on method
    String path = invocation
        .findAnnotationValueOnMethod(
            RequestMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Get mapping on method
    path = invocation
        .findAnnotationValueOnMethod(
            GetMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Post mapping on method
    path = invocation
        .findAnnotationValueOnMethod(
            PostMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Put mapping on method
    path = invocation
        .findAnnotationValueOnMethod(
            PutMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Patch mapping on method
    path = invocation
        .findAnnotationValueOnMethod(
            PatchMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Delete mapping on method
    path = invocation
        .findAnnotationValueOnMethod(
            DeleteMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    return clsPath + path;
  }
}
