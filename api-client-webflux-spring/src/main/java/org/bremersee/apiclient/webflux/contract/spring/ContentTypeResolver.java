package org.bremersee.apiclient.webflux.contract.spring;

import static org.bremersee.apiclient.webflux.Invocation.findAnnotationValue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class ContentTypeResolver implements Function<Invocation, Optional<MediaType>> {

  @Override
  public Optional<MediaType> apply(Invocation invocation) {
    Method method = invocation.getMethod();
    return Arrays.stream(
            findAnnotationValue(method, RequestMapping.class, a -> a.consumes().length > 0, RequestMapping::consumes)
                .or(() -> findAnnotationValue(
                    method, GetMapping.class, a -> a.consumes().length > 0, GetMapping::consumes))
                .or(() -> findAnnotationValue(
                    method, PostMapping.class, a -> a.consumes().length > 0, PostMapping::consumes))
                .or(() -> findAnnotationValue(
                    method, PutMapping.class, a -> a.consumes().length > 0, PutMapping::consumes))
                .or(() -> findAnnotationValue(
                    method, PatchMapping.class, a -> a.consumes().length > 0, PatchMapping::consumes))
                .or(() -> findAnnotationValue(
                    method, DeleteMapping.class, a -> a.consumes().length > 0, DeleteMapping::consumes))
                .orElse(new String[0]))
        .map(this::parseMediaType)
        .findFirst();
  }

  protected MediaType parseMediaType(String mediaType) {
    try {
      return MediaType.parseMediaType(mediaType);
    } catch (RuntimeException ignored) {
      return null;
    }
  }
}
