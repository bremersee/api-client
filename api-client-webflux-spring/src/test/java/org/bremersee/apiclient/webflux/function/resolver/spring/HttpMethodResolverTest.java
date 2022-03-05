package org.bremersee.apiclient.webflux.function.resolver.spring;

import java.lang.reflect.Method;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.function.HttpRequestMethod;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ExtendWith(SoftAssertionsExtension.class)
class HttpMethodResolverTest {

  private static final HttpMethodResolver target = new HttpMethodResolver();

  @Test
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodHead");
    Invocation invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.HEAD);

    method = Example.class.getMethod("methodOptions");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.OPTIONS);

    method = Example.class.getMethod("methodB");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.GET);

    method = Example.class.getMethod("methodC");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.POST);

    method = Example.class.getMethod("methodD");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.PUT);

    method = Example.class.getMethod("methodE");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.PATCH);

    method = Example.class.getMethod("methodF");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(HttpRequestMethod.DELETE);

    method = Example.class.getMethod("methodG");
    Invocation illegalInvocation = new Invocation(Example.class, method, null);
    softly.assertThatThrownBy(() -> target.apply(illegalInvocation))
        .isInstanceOf(ServiceException.class);
  }

  interface Example {

    @RequestMapping(method = RequestMethod.HEAD)
    void methodHead();

    @RequestMapping(method = RequestMethod.OPTIONS)
    void methodOptions();

    @GetMapping
    void methodB();

    @PostMapping
    void methodC();

    @PutMapping
    void methodD();

    @PatchMapping
    void methodE();

    @DeleteMapping
    void methodF();

    void methodG();

  }
}