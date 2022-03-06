package org.bremersee.apiclient.webflux.contract.spring;

import java.lang.reflect.Method;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
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
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", Sort.class);
    Sort sort = Sort.by(Order.by("a"), Order.by("b").with(Direction.DESC));
    Invocation invocation = new Invocation(Example.class, method, new Object[]{sort});
    MultiValueMap<String, Object> expected = new LinkedMultiValueMap<>();
    expected.add("sort", "a");
    expected.add("sort", "b,desc");

    MultiValueMap<String, Object> actual = target.apply(invocation);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);

    SortRequestParameterResolver configuredTarget = target
        .withRequestParamName("s")
        .withSeparatorValue(";")
        .withDescValue("d");

    expected = new LinkedMultiValueMap<>();
    expected.add("s", "a");
    expected.add("s", "b;d");

    actual = configuredTarget.apply(invocation);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);
  }

  interface Example {

    void methodA(Sort sort);

    void methodB(@RequestHeader Sort sort);

    void methodC(String id);
  }
}