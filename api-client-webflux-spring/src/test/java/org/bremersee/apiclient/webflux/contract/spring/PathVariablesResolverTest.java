package org.bremersee.apiclient.webflux.contract.spring;

import java.lang.reflect.Method;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class PathVariablesResolverTest {

  private static final PathVariablesResolver target = new PathVariablesResolver();

  @Test
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    Object value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, "nil"});
    Map<String, Object> actual = target.apply(invocation);
    softly.assertThat(actual)
        .isEqualTo(Map.of("id", value));
  }

  interface Example {

    void methodA(@PathVariable(name = "id") String id, @RequestParam(name = "name") String name);
  }
}