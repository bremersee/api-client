package org.bremersee.apiclient.webflux.contract.resolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class AbstractInvocationParameterResolverTest {

  @Test
  void toMultiValueMap(SoftAssertions softly) throws Exception {
    //noinspection unchecked
    AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>> target = Mockito
        .mock(AbstractInvocationParameterResolver.class);
    when(target.toMultiValueMap(any(), any(), any(), any()))
        .thenCallRealMethod();

    Object value = "test";
    Method method = Example.class.getMethod("junit", String.class, List.class, Map.class, int[].class);
    Invocation invocation = new Invocation(
        Example.class,
        method,
        new Object[]{value, null, null, null});
    Parameter parameter = method.getParameters()[0];
    RequestParam annotation = findAnnotation(parameter, RequestParam.class);
    InvocationParameter invocationParameter = new InvocationParameter(
        invocation,
        parameter,
        value,
        0);

    MultiValueMap<String, Object> actual = target.toMultiValueMap(
        invocationParameter, annotation, RequestParam::value, v -> v);
    softly.assertThat(actual)
        .isEqualTo(Map.of("id", List.of(value)));

    value = List.of("a", "b", "c");
    invocation = new Invocation(
        Example.class,
        method,
        new Object[]{null, value, null, null});
    parameter = method.getParameters()[1];
    annotation = findAnnotation(parameter, RequestParam.class);
    invocationParameter = new InvocationParameter(
        invocation,
        parameter,
        value,
        1);
    actual = target.toMultiValueMap(
        invocationParameter, annotation, RequestParam::value, v -> v);
    softly.assertThat(actual)
        .isEqualTo(Map.of("list", value));

    // value = null;
    invocation = new Invocation(
        Example.class,
        method,
        new Object[]{null, null, null, null});
    parameter = method.getParameters()[1];
    annotation = findAnnotation(parameter, RequestParam.class);
    invocationParameter = new InvocationParameter(
        invocation,
        parameter,
        null,
        1);
    actual = target.toMultiValueMap(
        invocationParameter, annotation, RequestParam::value, v -> v);
    softly.assertThat(actual)
        .isEqualTo(Map.of("list", List.of()));

    value = Map.of("a", "1", "b", "2");
    invocation = new Invocation(
        Example.class,
        method,
        new Object[]{null, null, value, null});
    parameter = method.getParameters()[2];
    annotation = findAnnotation(parameter, RequestParam.class);
    invocationParameter = new InvocationParameter(
        invocation,
        parameter,
        value,
        2);
    actual = target.toMultiValueMap(
        invocationParameter, annotation, RequestParam::value, v -> v);
    softly.assertThat(actual)
        .isEqualTo(Map.of("a", List.of("1"), "b", List.of("2")));

    value = new int[]{1, 2, 3};
    invocation = new Invocation(
        Example.class,
        method,
        new Object[]{null, null, null, value});
    parameter = method.getParameters()[2];
    annotation = findAnnotation(parameter, RequestParam.class);
    invocationParameter = new InvocationParameter(
        invocation,
        parameter,
        value,
        3);
    actual = target.toMultiValueMap(
        invocationParameter, annotation, RequestParam::value, v -> v);
    softly.assertThat(actual)
        .isEqualTo(Map.of("numbers", List.of(1, 2, 3)));
  }

  @SuppressWarnings("unused")
  static class Example {

    public void junit(
        @RequestParam(value = "id") String value,
        @RequestParam List<String> list,
        @RequestParam Map<String, String> map,
        @RequestParam int[] numbers) {

    }
  }

}