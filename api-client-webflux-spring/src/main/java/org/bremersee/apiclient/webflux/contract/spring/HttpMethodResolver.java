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

public class HttpMethodResolver implements Function<Invocation, HttpRequestMethod> {

  @Override
  public HttpRequestMethod apply(Invocation invocation) {

    Assert.notNull(invocation, "Invocation must be present.");
    Method method = invocation.getMethod();
    return Optional.ofNullable(findAnnotation(method, RequestMapping.class))
        .filter(requestMapping -> requestMapping.method().length > 0)
        .flatMap(requestMapping -> HttpRequestMethod.resolve(requestMapping.method()[0].name()))
        .or(() -> Optional.ofNullable(findAnnotation(method, GetMapping.class)).map(a -> HttpRequestMethod.GET))
        .or(() -> Optional.ofNullable(findAnnotation(method, PostMapping.class)).map(a -> HttpRequestMethod.POST))
        .or(() -> Optional.ofNullable(findAnnotation(method, PutMapping.class)).map(a -> HttpRequestMethod.PUT))
        .or(() -> Optional.ofNullable(findAnnotation(method, PatchMapping.class)).map(a -> HttpRequestMethod.PATCH))
        .or(() -> Optional.ofNullable(findAnnotation(method, DeleteMapping.class)).map(a -> HttpRequestMethod.DELETE))
        .orElseThrow(() -> new IllegalStateException(
            String.format("Cannot find request method on method '%s'.", method.getName())));
  }
}
