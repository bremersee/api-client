package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class InvocationParameterTest {

  @Test
  void getParameterName() throws Exception {
    InvocationParameter target = createTarget("123");
    assertThat(target.getParameterName())
        .matches(name -> "id".equals(name) || "arg0".equals(name));
  }

  @Test
  void hasNoneParameterAnnotation(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("123");
    //noinspection unchecked
    boolean actual = target.hasNoneParameterAnnotation(PathVariable.class);
    softly.assertThat(actual)
        .isFalse();
    //noinspection unchecked
    actual = target.hasNoneParameterAnnotation(RequestParam.class);
    softly.assertThat(actual)
        .isTrue();
  }

  @Test
  void hasParameterAnnotation(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("123");
    boolean actual = target.hasParameterAnnotation(RequestHeader.class);
    softly.assertThat(actual)
        .isFalse();
    actual = target.hasParameterAnnotation(PathVariable.class);
    softly.assertThat(actual)
        .isTrue();
  }

  @Test
  void findParameterAnnotation(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("123");
    Optional<? extends Annotation> actual = target.findParameterAnnotation(RequestParam.class);
    softly.assertThat(actual)
        .isEmpty();
    actual = target.findParameterAnnotation(PathVariable.class);
    softly.assertThat(actual)
        .isPresent();
  }

  @Test
  void testToString(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("123");
    String actual = target.toString();
    softly.assertThat(actual)
        .contains("123");
    softly.assertThat(actual)
        .contains("0");
  }

  @Test
  void get(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("abc");
    softly.assertThat(target.getParameter())
        .isNotNull();
    softly.assertThat(target.getValue())
        .isEqualTo("abc");
    softly.assertThat(target.getIndex())
        .isEqualTo(0);
  }

  private Invocation createInvocation(String value) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    return new Invocation(InvocationTest.Example.class, method, new Object[]{value});
  }

  private InvocationParameter createTarget(String value) throws Exception {
    Invocation invocation = createInvocation(value);
    return new InvocationParameter(createInvocation(value), invocation.getMethod().getParameters()[0], value, 0);
  }

  interface Example {

    void methodA(@PathVariable String id);
  }
}