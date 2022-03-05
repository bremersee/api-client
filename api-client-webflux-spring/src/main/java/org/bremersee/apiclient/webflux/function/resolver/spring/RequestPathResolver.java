package org.bremersee.apiclient.webflux.function.resolver.spring;

import static org.bremersee.apiclient.webflux.function.Invocation.findAnnotationValue;

import java.util.function.Function;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class RequestPathResolver implements Function<Invocation, String> {

  @Override
  public String apply(Invocation invocation) {
    // Request mapping on class
    String clsPath = findAnnotationValue(
        invocation.getTargetClass(), RequestMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");

    // Request mapping on method
    String path = findAnnotationValue(
        invocation.getMethod(), RequestMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Get mapping on method
    path = findAnnotationValue(
        invocation.getMethod(), GetMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Post mapping on method
    path = findAnnotationValue(
        invocation.getMethod(), PostMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Put mapping on method
    path = findAnnotationValue(
        invocation.getMethod(), PutMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Patch mapping on method
    path = findAnnotationValue(
        invocation.getMethod(), PatchMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    if (StringUtils.hasText(path)) {
      return clsPath + path;
    }

    // Delete mapping on method
    path = findAnnotationValue(
        invocation.getMethod(), DeleteMapping.class, a -> a.value().length > 0, a -> a.value()[0])
        .orElse("");
    return clsPath + path;
  }
}
