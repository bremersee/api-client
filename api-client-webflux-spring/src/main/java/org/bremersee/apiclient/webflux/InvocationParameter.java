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
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@EqualsAndHashCode(callSuper = true)
public class InvocationParameter extends Invocation {

  private final Parameter parameter;

  private final Object value;

  private final int index;

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

  @SuppressWarnings("unchecked")
  public boolean hasNoneParameterAnnotation(Class<? extends Annotation>... annotationTypes) {
    if (isEmpty(annotationTypes)) {
      return false;
    }
    return Arrays.stream(annotationTypes).noneMatch(this::hasParameterAnnotation);
  }

  public boolean hasParameterAnnotation(Class<? extends Annotation> annotationType) {
    return findParameterAnnotation(annotationType).isPresent();
  }

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

  private <E> MultiValueMap<String, E> toMultiValueMap(Map<?, ?> map, Function<Object, E> valueMapper) {
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
