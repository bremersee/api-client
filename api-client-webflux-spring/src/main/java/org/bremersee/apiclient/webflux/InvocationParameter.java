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

package org.bremersee.apiclient.webflux;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ObjectUtils.isArray;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.ObjectUtils.toObjectArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The invocation parameter.
 *
 * @author Christian Bremer
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class InvocationParameter extends Invocation {

  private final Parameter parameter;

  private final Object value;

  private final int index;

  /**
   * Instantiates a new invocation parameter.
   *
   * @param invocation the invocation
   * @param parameter the parameter
   * @param value the value
   * @param index the index
   */
  public InvocationParameter(Invocation invocation, Parameter parameter, Object value, int index) {
    super(invocation.getTargetClass(), invocation.getMethod(), invocation.getArgs());
    Assert.notNull(parameter, "Parameter must be present.");
    Assert.isTrue(
        index >= 0 && index < invocation.getMethod().getParameters().length,
        String.format("Illegal index [%s].", index));
    this.parameter = parameter;
    this.value = value;
    this.index = index;
  }

  /**
   * Gets parameter name.
   *
   * @return the parameter name
   */
  public String getParameterName() {
    try {
      //noinspection ConstantConditions
      String name = new DefaultParameterNameDiscoverer()
          .getParameterNames(getMethod())[index];
      if (!isEmpty(name)) {
        return name;
      }
    } catch (Exception ignored) {
      // ignored
    }
    try {
      //noinspection ConstantConditions
      String name = new LocalVariableTableParameterNameDiscoverer()
          .getParameterNames(getMethod())[index];
      if (!isEmpty(name)) {
        return name;
      }
    } catch (Exception ignored) {
      // ignored
    }
    return parameter.getName();
  }

  /**
   * Has none parameter annotation.
   *
   * @param annotationTypes the annotation types
   * @return the boolean
   */
  public boolean hasNoneParameterAnnotation(Set<Class<? extends Annotation>> annotationTypes) {
    if (isEmpty(annotationTypes)) {
      return true;
    }
    return annotationTypes.stream().noneMatch(this::hasParameterAnnotation);
  }

  /**
   * Has parameter annotation.
   *
   * @param annotationType the annotation type
   * @return the boolean
   */
  public boolean hasParameterAnnotation(Class<? extends Annotation> annotationType) {
    return findParameterAnnotation(annotationType).isPresent();
  }

  /**
   * Find parameter annotation.
   *
   * @param <A> the type parameter
   * @param annotationType the annotation type
   * @return the optional
   */
  public <A extends Annotation> Optional<A> findParameterAnnotation(Class<A> annotationType) {
    return Optional.ofNullable(findAnnotation(parameter, annotationType));
  }

  private <A extends Annotation> String getKey(
      A annotation,
      Function<A, String> keyExtractor) {

    return Optional.ofNullable(annotation)
        .map(keyExtractor)
        .filter(name -> !name.isBlank())
        .orElseGet(this::getParameterName);
  }

  /**
   * To multi value map.
   *
   * @param <E> the type parameter
   * @param <A> the type parameter
   * @param annotationType the annotation type
   * @param keyExtractor the key extractor
   * @param valueMapper the value mapper
   * @return the multi value map
   */
  public <E, A extends Annotation> MultiValueMap<String, E> toMultiValueMap(
      Class<A> annotationType,
      Function<A, String> keyExtractor,
      Function<Object, E> valueMapper) {

    return findParameterAnnotation(annotationType)
        .map(annotation -> {
          MultiValueMap<String, E> map = new LinkedMultiValueMap<>();
          Object value = getValue();
          if (value instanceof Map<?, ?>) {
            map.putAll(toMultiValueMap((Map<?, ?>) value, valueMapper));
          } else {
            String key = getKey(
                annotation,
                keyExtractor);
            map.put(key, toList(value, valueMapper));
          }
          return map;
        })
        .orElseGet(LinkedMultiValueMap::new);
  }

  private <E> MultiValueMap<String, E> toMultiValueMap(
      Map<?, ?> map, Function<Object, E> valueMapper) {
    MultiValueMap<String, E> multiValueMap = new LinkedMultiValueMap<>();
    if (!isEmpty(map)) {
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        String key = String.valueOf(entry.getKey());
        List<E> value = toList(entry.getValue(), valueMapper);
        if (!isEmpty(value)) {
          multiValueMap.addAll(key, value);
        }
      }
    }
    return multiValueMap;
  }

  private <E> List<E> toList(Object value, Function<Object, E> valueMapper) {
    List<E> list = new ArrayList<>();
    if (isEmpty(value)) {
      return list;
    }
    if (isArray(value)) {
      return Arrays.stream(toObjectArray(value))
          .filter(Objects::nonNull)
          .map(valueMapper)
          .collect(Collectors.toList());
    }
    if (value instanceof Collection<?>) {
      return ((Collection<?>) value).stream()
          .filter(Objects::nonNull)
          .map(valueMapper)
          .collect(Collectors.toList());
    }
    list.add(valueMapper.apply(value));
    return list;
  }

  @Override
  public String toString() {
    return "InvocationParameter{"
        + "targetClass=" + getTargetClass().getName()
        + ", method=" + getMethod().getName()
        + ", parameter=" + getParameterName()
        + ", value=" + value
        + ", index=" + index
        + '}';
  }
}
