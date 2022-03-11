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

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.contract.HttpRequestMethod;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The http method resolver.
 *
 * @author Christian Bremer
 */
public class HttpMethodResolver implements Function<Invocation, HttpRequestMethod> {

  @Override
  public HttpRequestMethod apply(Invocation invocation) {

    Assert.notNull(invocation, "Invocation must be present.");
    Method method = invocation.getMethod();
    return Optional.ofNullable(findAnnotation(method, RequestMapping.class))
        .filter(requestMapping -> requestMapping.method().length > 0)
        .flatMap(requestMapping -> HttpRequestMethod.resolve(requestMapping.method()[0].name()))
        .or(() -> Optional.ofNullable(findAnnotation(method, GetMapping.class))
            .map(a -> HttpRequestMethod.GET))
        .or(() -> Optional.ofNullable(findAnnotation(method, PostMapping.class))
            .map(a -> HttpRequestMethod.POST))
        .or(() -> Optional.ofNullable(findAnnotation(method, PutMapping.class))
            .map(a -> HttpRequestMethod.PUT))
        .or(() -> Optional.ofNullable(findAnnotation(method, PatchMapping.class))
            .map(a -> HttpRequestMethod.PATCH))
        .or(() -> Optional.ofNullable(findAnnotation(method, DeleteMapping.class))
            .map(a -> HttpRequestMethod.DELETE))
        .orElseThrow(() -> new IllegalStateException(
            String.format("Cannot find request method on method '%s'.", method.getName())));
  }
}
