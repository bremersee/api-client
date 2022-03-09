package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class InvocationParameterTest {

  @Test
  void getParameterName() throws Exception {
    InvocationParameter target = createTarget("methodA", String.class, "123");
    assertThat(target.getParameterName())
        .matches(name -> "id".equals(name) || "arg0".equals(name));
  }

  @Test
  void hasNoneParameterAnnotation(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("methodA", String.class, "123");
    boolean actual = target.hasNoneParameterAnnotation(Set.of(PathVariable.class));
    softly.assertThat(actual)
        .isFalse();
    actual = target.hasNoneParameterAnnotation(Set.of(RequestParam.class));
    softly.assertThat(actual)
        .isTrue();
    softly.assertThat(target.hasNoneParameterAnnotation(Set.of()))
        .isTrue();
  }

  @Test
  void hasParameterAnnotation(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("methodA", String.class, "123");
    boolean actual = target.hasParameterAnnotation(RequestHeader.class);
    softly.assertThat(actual)
        .isFalse();
    actual = target.hasParameterAnnotation(PathVariable.class);
    softly.assertThat(actual)
        .isTrue();
  }

  @Test
  void findParameterAnnotation(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("methodA", String.class, "123");
    Optional<? extends Annotation> actual = target.findParameterAnnotation(RequestParam.class);
    softly.assertThat(actual)
        .isEmpty();
    actual = target.findParameterAnnotation(PathVariable.class);
    softly.assertThat(actual)
        .isPresent();
  }

  @Test
  void toMultiValueMapOfNull() throws Exception {
    InvocationParameter target = createTarget("methodB", String.class, null);
    MultiValueMap<String, Object> actual = target.toMultiValueMap(RequestParam.class, RequestParam::value, v -> v);
    assertThat(actual)
        .isEqualTo(Map.of("name", List.of()));
  }

  @Test
  void toMultiValueMapOfSingleValue() throws Exception {
    InvocationParameter target = createTarget("methodB", String.class, "456");
    MultiValueMap<String, Object> actual = target.toMultiValueMap(RequestParam.class, RequestParam::value, v -> v);
    assertThat(actual)
        .isEqualTo(Map.of("name", List.of("456")));
  }

  @Test
  void toMultiValueMapOfList() throws Exception {
    InvocationParameter target = createTarget("methodC", List.class, List.of("456", "789"));
    MultiValueMap<String, Object> actual = target.toMultiValueMap(RequestParam.class, RequestParam::value, v -> v);
    assertThat(actual)
        .isEqualTo(Map.of("name", List.of("456", "789")));
  }

  @Test
  void toMultiValueMapOfArray() throws Exception {
    InvocationParameter target = createTarget("methodD", int[].class, new int[]{1, 2, 3});
    MultiValueMap<String, Object> actual = target.toMultiValueMap(RequestParam.class, RequestParam::value, v -> v);
    assertThat(actual)
        .isEqualTo(Map.of("numbers", List.of(1, 2, 3)));
  }

  @Test
  void toMultiValueMap() throws Exception {
    Map<String, Object> source = new LinkedHashMap<>();
    source.put("name", "123");
    source.put("sort", List.of("lastName", "firstName"));
    source.put("numbers", new int[]{5, 6, 7});
    InvocationParameter target = createTarget("methodE", Map.class, source);
    MultiValueMap<String, Object> actual = target.toMultiValueMap(RequestParam.class, RequestParam::value, v -> v);
    MultiValueMap<String, Object> expected = new LinkedMultiValueMap<>();
    expected.add("name", "123");
    expected.addAll("sort", List.of("lastName", "firstName"));
    expected.addAll("numbers", List.of(5, 6, 7));
    assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);
  }

  @Test
  void testToString(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("methodA", String.class, "123");
    String actual = target.toString();
    softly.assertThat(actual)
        .contains("123");
    softly.assertThat(actual)
        .contains("0");
  }

  @Test
  void get(SoftAssertions softly) throws Exception {
    InvocationParameter target = createTarget("methodA", String.class, "abc");
    softly.assertThat(target.getParameter())
        .isNotNull();
    softly.assertThat(target.getValue())
        .isEqualTo("abc");
    softly.assertThat(target.getIndex())
        .isEqualTo(0);
  }

  private Invocation createInvocation(String methodName, Class<?> paramClass, Object value) throws Exception {
    Method method = Example.class.getMethod(methodName, paramClass);
    return new Invocation(InvocationTest.Example.class, method, new Object[]{value});
  }

  private InvocationParameter createTarget(String methodName, Class<?> paramClass, Object value) throws Exception {
    Invocation invocation = createInvocation(methodName, paramClass, value);
    return new InvocationParameter(
        createInvocation(methodName, paramClass, value),
        invocation.getMethod().getParameters()[0], value, 0);
  }

  @SuppressWarnings("unused")
  interface Example {

    void methodA(@PathVariable String id);

    void methodB(@RequestParam(name = "name") String name);

    void methodC(@RequestParam(name = "name") List<String> names);

    void methodD(@RequestParam(name = "numbers") int[] numbers);

    void methodE(@RequestParam Map<String, Object> params);
  }

}