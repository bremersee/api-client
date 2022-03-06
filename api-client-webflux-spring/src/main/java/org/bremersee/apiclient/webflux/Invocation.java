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

package org.bremersee.apiclient.webflux;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.Assert;

/**
 * The invocation.
 *
 * @author Christian Bremer
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Invocation {

  @NonNull
  private final Class<?> targetClass;

  @NonNull
  private final Method method;

  private final Object[] args;

  @Override
  public String toString() {
    return "Invocation{"
        + "targetClass=" + targetClass.getName()
        + ", method=" + method.getName()
        + ", args=" + Arrays.toString(args)
        + '}';
  }

  public Stream<InvocationParameter> toMethodParameterStream() {
    List<InvocationParameter> invocationParameters = new ArrayList<>();
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      invocationParameters.add(new InvocationParameter(this, parameters[i], args[i], i));
    }
    return invocationParameters.stream();
  }

  public static <T, A extends Annotation> Optional<T> findAnnotationValue(
      AnnotatedElement annotatedElement,
      Class<A> annotationType,
      Predicate<A> condition,
      Function<A, T> mapper) {

    Assert.notNull(annotatedElement, "Annotated element must be present.");
    Assert.notNull(annotationType, "Annotation type must be present.");
    Assert.notNull(condition, "Condition must be present.");
    Assert.notNull(mapper, "Mapper must be present.");

    return Optional.of(annotatedElement)
        .map(m -> findAnnotation(m, annotationType))
        .filter(condition)
        .map(mapper);
  }

}
