package org.bremersee.apiclient.webflux.function;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@Getter
@EqualsAndHashCode(callSuper = true)
public class InvocationParameter extends Invocation {

  private final Parameter parameter;

  private final Object value;

  private final int index;

  public InvocationParameter(Invocation invocation, Parameter parameter, Object value, int index) {
    super(invocation.getTargetClass(), invocation.getMethod(), invocation.getArgs());
    Assert.notNull(parameter, "Parameter must be present.");
    this.parameter = parameter;
    this.value = value;
    this.index = index;
  }

  @SuppressWarnings("unchecked")
  public boolean hasNoneParameterAnnotation(Class<? extends Annotation>... annotationTypes) {
    if (ObjectUtils.isEmpty(annotationTypes)) {
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

  @Override
  public String toString() {
    return "InvocationParameter{"
        + "targetClass=" + getTargetClass().getName()
        + ", method=" + getMethod().getName()
        + ", parameter=" + parameter.getName()
        + ", value=" + value
        + ", index=" + index
        + '}';
  }
}
