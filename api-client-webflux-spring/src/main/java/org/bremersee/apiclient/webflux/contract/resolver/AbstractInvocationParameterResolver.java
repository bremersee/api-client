package org.bremersee.apiclient.webflux.contract.resolver;

import static org.springframework.util.ObjectUtils.isArray;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.ObjectUtils.toObjectArray;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public abstract class AbstractInvocationParameterResolver<E, T extends MultiValueMap<String, E>>
    implements InvocationParameterResolver<E, T> {

  private <A extends Annotation> String getKey(
      InvocationParameter invocationParameter,
      A annotation,
      Function<A, String> keyExtractor) {

    return Optional.ofNullable(annotation)
        .map(keyExtractor)
        .filter(name -> !name.isBlank())
        .orElseGet(invocationParameter::getParameterName);
  }

  protected <A extends Annotation> MultiValueMap<String, E> toMultiValueMap(
      InvocationParameter invocationParameter,
      A annotation,
      Function<A, String> keyExtractor,
      Function<Object, E> valueMapper) {

    MultiValueMap<String, E> map = new LinkedMultiValueMap<>();
    Object value = invocationParameter.getValue();
    if (value instanceof Map<?, ?>) {
      map.putAll(toMultiValueMap((Map<?, ?>) value, valueMapper));
    } else {
      String key = getKey(
          invocationParameter,
          annotation,
          keyExtractor);
      map.put(key, toList(value, valueMapper));
    }
    return map;
  }

  private MultiValueMap<String, E> toMultiValueMap(Map<?, ?> map, Function<Object, E> valueMapper) {
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

  private List<E> toList(Object value, Function<Object, E> valueMapper) {
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

}
