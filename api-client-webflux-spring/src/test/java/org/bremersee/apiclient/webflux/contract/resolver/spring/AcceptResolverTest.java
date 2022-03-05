package org.bremersee.apiclient.webflux.contract.resolver.spring;

import java.lang.reflect.Method;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ExtendWith(SoftAssertionsExtension.class)
class AcceptResolverTest {

  private static final AcceptResolver target = new AcceptResolver();

  @Test
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA");
    Invocation invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(MediaType.APPLICATION_JSON);

    method = Example.class.getMethod("methodB");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(MediaType.APPLICATION_JSON);

    method = Example.class.getMethod("methodC");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(MediaType.APPLICATION_JSON);

    method = Example.class.getMethod("methodD");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(MediaType.APPLICATION_JSON);

    method = Example.class.getMethod("methodE");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(MediaType.APPLICATION_XML);

    method = Example.class.getMethod("methodF");
    invocation = new Invocation(Example.class, method, null);
    softly.assertThat(target.apply(invocation))
        .isEqualTo(MediaType.ALL);
  }

  @Test
  void parseMediaType(SoftAssertions softly) {
    softly.assertThat(target.parseMediaType("application/json"))
        .isEqualTo(MediaType.APPLICATION_JSON);

    softly.assertThat(target.parseMediaType("/json"))
        .isNull();
  }

  interface Example {

    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodA();

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodB();

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodC();

    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodD();

    @PatchMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void methodE();

    @DeleteMapping
    void methodF();

  }
}