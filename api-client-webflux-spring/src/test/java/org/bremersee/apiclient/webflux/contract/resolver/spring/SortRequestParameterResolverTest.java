package org.bremersee.apiclient.webflux.contract.resolver.spring;

import java.lang.reflect.Method;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;

@ExtendWith(SoftAssertionsExtension.class)
class SortRequestParameterResolverTest {

  private static final SortRequestParameterResolver target = new SortRequestParameterResolver();

  @Test
  void canResolve(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", Sort.class);
    Object value = Sort.by(Order.by("a"), Order.by("b").with(Direction.DESC));
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);

    softly.assertThat(target.canResolve(invocationParameter))
        .isTrue();

    method = Example.class.getMethod("methodB", Sort.class);
    invocation = new Invocation(Example.class, method, new Object[]{value});
    invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);
    softly.assertThat(target.canResolve(invocationParameter))
        .isFalse();

    value = "a";
    method = Example.class.getMethod("methodC", String.class);
    invocation = new Invocation(Example.class, method, new Object[]{value});
    invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);
    softly.assertThat(target.canResolve(invocationParameter))
        .isFalse();
  }

  @Test
  void resolve(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", Sort.class);
    Sort sort = Sort.by(Order.by("a"), Order.by("b").with(Direction.DESC));
    Invocation invocation = new Invocation(Example.class, method, new Object[]{sort});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        sort,
        0);
    MultiValueMap<String, Object> expected = new LinkedMultiValueMap<>();
    expected.add("sort", "a");
    expected.add("sort", "b,desc");

    MultiValueMap<String, Object> actual = target.resolve(invocationParameter);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);

    SortRequestParameterResolver configuredTarget = target
        .withRequestParamName("s")
        .withSeparatorValue(";")
        .withDescValue("d");

    expected = new LinkedMultiValueMap<>();
    expected.add("s", "a");
    expected.add("s", "b;d");

    actual = configuredTarget.resolve(invocationParameter);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);
  }

  interface Example {

    void methodA(Sort sort);

    void methodB(@RequestHeader Sort sort);

    void methodC(String id);
  }
}