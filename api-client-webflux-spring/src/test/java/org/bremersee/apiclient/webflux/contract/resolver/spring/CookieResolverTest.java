package org.bremersee.apiclient.webflux.contract.resolver.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class CookieResolverTest {

  private static final CookieResolver target = new CookieResolver();

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
  void resolve() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    String value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, null});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);
    MultiValueMap<String, String> actual = target.resolve(invocationParameter);
    assertThat(actual)
        .isEqualTo(Map.of("id", List.of(value)));
  }

  interface Example {

    void methodA(@CookieValue(name = "id") String id, @RequestParam String name);
  }

}