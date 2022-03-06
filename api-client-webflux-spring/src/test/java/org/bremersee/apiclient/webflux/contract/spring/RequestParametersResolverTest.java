package org.bremersee.apiclient.webflux.contract.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class RequestParametersResolverTest {

  private static final RequestParametersResolver target = new RequestParametersResolver();

  @Test
  void apply() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    Object value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, null});
    MultiValueMap<String, Object> actual = target.apply(invocation);
    assertThat(actual)
        .isEqualTo(Map.of("name", List.of(value)));
  }

  interface Example {

    void methodA(@RequestParam(name = "name") String name, @PathVariable(name = "id") String id);
  }
}