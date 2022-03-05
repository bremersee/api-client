package org.bremersee.apiclient.webflux.contract.resolver.spring;

import java.lang.reflect.Method;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.resolver.spring.RequestBodyResolver.PossibleRequestBodyPredicate;
import org.bremersee.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class RequestBodyResolverTest {

  @Test
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", Long.class, String.class);
    Object value = "abc";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{1L, value});
    InvocationParameter expected = invocation.toMethodParameterStream()
        .filter(i -> i.getIndex() == 1)
        .findFirst()
        .orElseThrow();
    RequestBodyResolver target = new RequestBodyResolver();
    Optional<InvocationParameter> actual = target.apply(invocation);
    softly.assertThat(actual)
        .hasValue(expected);

    method = Example.class.getMethod("methodAa", String.class, Long.class);
    invocation = new Invocation(Example.class, method, new Object[]{value, 1L});
    expected = invocation.toMethodParameterStream()
        .filter(i -> i.getIndex() == 0)
        .findFirst()
        .orElseThrow();
    actual = target.apply(invocation);
    softly.assertThat(actual)
        .hasValue(expected);

    method = Example.class.getMethod("methodB", String.class);
    invocation = new Invocation(Example.class, method, new Object[]{value});
    expected = invocation.toMethodParameterStream()
        .filter(i -> i.getIndex() == 0)
        .findFirst()
        .orElseThrow();
    actual = target.apply(invocation);
    softly.assertThat(actual)
        .hasValue(expected);

    method = Example.class.getMethod("methodC", Long.class, String.class);
    Invocation invocationMethodC = new Invocation(Example.class, method, new Object[]{1L, value});
    softly.assertThatThrownBy(() -> target.apply(invocationMethodC))
        .isInstanceOf(ServiceException.class)
        .extracting("errorCode")
        .isEqualTo("org.bremersee:api-client:9b12803a-e54e-4d3a-85cd-5377d9f2355b");

    RequestBodyResolver targetMethodC = target.withPossibleBodyPredicate(new PossibleRequestBodyPredicate()
        .add(invocationParameter -> Long.class
            .isAssignableFrom(invocationParameter.getParameter().getType())));
    invocation = new Invocation(Example.class, method, new Object[]{1L, value});
    expected = invocation.toMethodParameterStream()
        .filter(i -> i.getIndex() == 1)
        .findFirst()
        .orElseThrow();
    actual = targetMethodC.apply(invocation);
    softly.assertThat(actual)
        .hasValue(expected);

    method = Example.class.getMethod("methodD", String.class, String.class);
    Invocation invocationMethodD = new Invocation(Example.class, method, new Object[]{"1", value});
    softly.assertThatThrownBy(() -> target.apply(invocationMethodD))
        .isInstanceOf(ServiceException.class)
        .extracting("errorCode")
        .isEqualTo("org.bremersee:api-client:47dbf2f1-1d89-43d2-8eab-16402477b8aa");

    method = Example.class.getMethod("methodE", String.class, String.class, String.class, String.class);
    invocation = new Invocation(Example.class, method, new Object[]{value, null, null, null});
    actual = target.apply(invocation);
    softly.assertThat(actual)
        .isEmpty();
  }

  interface Example {

    void methodA(Long id, @RequestBody String body);

    void methodAa(@RequestBody String body, Long id);

    void methodB(String body);

    void methodC(Long id, String body);

    void methodD(@RequestBody String id, @RequestBody String body);

    void methodE(
        @PathVariable(name = "a") String a,
        @RequestParam(name = "b") String b,
        @RequestHeader(name = "Authorization") String c,
        @CookieValue(name = "d") String d);
  }
}