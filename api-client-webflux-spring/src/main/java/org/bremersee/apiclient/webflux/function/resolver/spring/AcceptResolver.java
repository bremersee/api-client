package org.bremersee.apiclient.webflux.function.resolver.spring;

import static org.bremersee.apiclient.webflux.function.Invocation.findAnnotationValue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class AcceptResolver implements Function<Invocation, MediaType> {

  @Override
  public MediaType apply(Invocation parameters) {
    Method method = parameters.getMethod();
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
