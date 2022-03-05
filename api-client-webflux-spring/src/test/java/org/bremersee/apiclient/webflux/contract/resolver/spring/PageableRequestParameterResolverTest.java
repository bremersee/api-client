package org.bremersee.apiclient.webflux.contract.resolver.spring;

import java.lang.reflect.Method;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

@ExtendWith(SoftAssertionsExtension.class)
class PageableRequestParameterResolverTest {

  private static final PageableRequestParameterResolver target = new PageableRequestParameterResolver();

  @Test
  void canResolve(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", Pageable.class);
    Object value = PageRequest.of(4, 25, Sort.by(Order.by("a"), Order.by("b").with(Direction.DESC)));
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        value,
        0);

    softly.assertThat(target.canResolve(invocationParameter))
        .isTrue();

    method = Example.class.getMethod("methodB", Pageable.class);
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
    Method method = Example.class.getMethod("methodA", Pageable.class);
    Pageable pageable = PageRequest.of(4, 25, Sort.by(Order.by("a"), Order.by("b").with(Direction.DESC)));
    Invocation invocation = new Invocation(Example.class, method, new Object[]{pageable});
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        method.getParameters()[0],
        pageable,
        0);
    MultiValueMap<String, Object> expected = new LinkedMultiValueMap<>();
    expected.add("sort", "a");
    expected.add("sort", "b,desc");
    expected.add("page", 4);
    expected.add("size", 25);

    MultiValueMap<String, Object> actual = target.resolve(invocationParameter);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);

    PageableRequestParameterResolver configuredTarget = target.withSortRequestParameterResolver(
            new SortRequestParameterResolver()
                .withRequestParamName("s")
                .withSeparatorValue(";")
                .withDescValue("d"))
        .withPageNumberRequestParamName("p")
        .withPageSizeRequestParamName("z");

    expected = new LinkedMultiValueMap<>();
    expected.add("s", "a");
    expected.add("s", "b;d");
    expected.add("p", 4);
    expected.add("z", 25);

    actual = configuredTarget.resolve(invocationParameter);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);
  }

  interface Example {

    void methodA(Pageable pageRequest);

    void methodB(@RequestBody Pageable pageRequest);

    void methodC(String id);
  }
}