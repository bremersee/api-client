package org.bremersee.apiclient.webflux.function.resolver.spring;

import java.lang.reflect.Method;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ExtendWith(SoftAssertionsExtension.class)
class RequestPathResolverTest {

  private static final RequestPathResolver target = new RequestPathResolver();

  @Test
  void apply(SoftAssertions softly) throws Exception {
    for (Class<?> clazz : List.of(ExampleA.class, ExampleB.class)) {
      Method method = clazz.getMethod("methodA");
      Invocation invocation = new Invocation(clazz, method, null);
      String actual = target.apply(invocation);
      softly.assertThat(actual)
          .isEqualTo("/api/a");

      method = clazz.getMethod("methodB");
      invocation = new Invocation(clazz, method, null);
      actual = target.apply(invocation);
      softly.assertThat(actual)
          .isEqualTo("/api/b");

      method = clazz.getMethod("methodC");
      invocation = new Invocation(clazz, method, null);
      actual = target.apply(invocation);
      softly.assertThat(actual)
          .isEqualTo("/api/c");

      method = clazz.getMethod("methodD");
      invocation = new Invocation(clazz, method, null);
      actual = target.apply(invocation);
      softly.assertThat(actual)
          .isEqualTo("/api/d");

      method = clazz.getMethod("methodE");
      invocation = new Invocation(clazz, method, null);
      actual = target.apply(invocation);
      softly.assertThat(actual)
          .isEqualTo("/api/e");

      method = clazz.getMethod("methodF");
      invocation = new Invocation(clazz, method, null);
      actual = target.apply(invocation);
      softly.assertThat(actual)
          .isEqualTo("/api/f");
    }
  }

  @RequestMapping(value = "/api")
  interface ExampleA {

    @RequestMapping(value = "/a")
    void methodA();

    @GetMapping(value = "/b")
    void methodB();

    @PostMapping(value = "/c")
    void methodC();

    @PutMapping(value = "/d")
    void methodD();

    @PatchMapping(value = "/e")
    void methodE();

    @DeleteMapping(value = "/f")
    void methodF();
  }

  @RequestMapping(path = "/api")
  interface ExampleB {

    @RequestMapping(path = "/a")
    void methodA();

    @GetMapping(path = "/b")
    void methodB();

    @PostMapping(path = "/c")
    void methodC();

    @PutMapping(path = "/d")
    void methodD();

    @PatchMapping(path = "/e")
    void methodE();

    @DeleteMapping(path = "/f")
    void methodF();
  }
}