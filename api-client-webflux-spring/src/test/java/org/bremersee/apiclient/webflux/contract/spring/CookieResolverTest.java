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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class CookieResolverTest {

  private static final CookiesResolver target = new CookiesResolver();

  @Test
  void resolve() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    String value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, null});
    MultiValueMap<String, String> actual = target.apply(invocation);
    assertThat(actual)
        .isEqualTo(Map.of("id", List.of(value)));
  }

  interface Example {

    void methodA(@CookieValue(name = "id") String id, @RequestParam(name = "name") String name);
  }

}