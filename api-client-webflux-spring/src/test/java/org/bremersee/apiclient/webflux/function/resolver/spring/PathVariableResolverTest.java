package org.bremersee.apiclient.webflux.function.resolver.spring;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class PathVariableResolverTest {

  private static final PathVariableResolver target = new PathVariableResolver();

  @Test
  void canResolve(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    Object value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, null});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);

    softly.assertThat(target.canResolve(invocationParameter))
        .isTrue();

    invocation = new Invocation(Example.class, method, new Object[]{value});
    invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[1],
        value,
        1);

    softly.assertThat(target.canResolve(invocationParameter))
        .isFalse();
  }

  @Test
  void resolve(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    Object value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, null});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);
    MultiValueMap<String, Object> actual = target.resolve(invocationParameter);
    softly.assertThat(actual)
        .isEqualTo(Map.of("id", List.of(value)));

    Invocation invocationWithNull = new Invocation(Example.class, method, new Object[]{null, null});
    InvocationParameter invocationParameterWithNull = new InvocationParameter(
        invocationWithNull,
        method.getParameters()[0],
        null,
        0);
    softly.assertThatThrownBy(() -> target.resolve(invocationParameterWithNull))
        .isInstanceOf(ServiceException.class);
  }

  interface Example {

    void methodA(@PathVariable(name = "id") String id, @RequestParam String name);
  }
}