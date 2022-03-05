package org.bremersee.apiclient.webflux.contract;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.Assert;

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
