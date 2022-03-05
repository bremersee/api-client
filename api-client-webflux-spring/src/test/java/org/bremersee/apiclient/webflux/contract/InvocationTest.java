package org.bremersee.apiclient.webflux.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.PathVariable;

@ExtendWith(SoftAssertionsExtension.class)
class InvocationTest {

  @Test
  void testToString(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"abc"});
    String actual = invocation.toString();
    softly.assertThat(actual)
        .contains("Example");
    softly.assertThat(actual)
        .contains("methodA");
    softly.assertThat(actual)
        .contains("abc");
  }

  @Test
  void toMethodParameterStream() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"abc"});
    List<InvocationParameter> actual = invocation.toMethodParameterStream()
        .collect(Collectors.toList());
    List<InvocationParameter> expected = List
        .of(new InvocationParameter(invocation, method.getParameters()[0], "abc", 0));
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void findAnnotationValue() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Optional<String> actual = Invocation.findAnnotationValue(
        method.getParameters()[0],
        PathVariable.class,
        pathVariable -> !pathVariable.name().isEmpty(),
        PathVariable::name);
    Assertions.assertThat(actual)
        .hasValue("id");
  }

  @Test
  void get(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"abc"});
    softly.assertThat(invocation.getTargetClass())
        .isEqualTo(Example.class);
    softly.assertThat(invocation.getMethod())
        .isEqualTo(method);
    softly.assertThat(invocation.getArgs())
        .isEqualTo(new Object[]{"abc"});
  }

  interface Example {

    void methodA(@PathVariable(name = "id") String id);
  }
}