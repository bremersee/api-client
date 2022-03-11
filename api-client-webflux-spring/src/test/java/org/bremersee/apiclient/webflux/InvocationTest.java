/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.apiclient.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The invocation test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class InvocationTest {

  /**
   * Test to string.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
  @Test
  void testToString(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"abc"});
    String actual = invocation.toString();
    softly.assertThat(actual)
        .contains("Example");
    softly.assertThat(actual)
        .contains("methodA");
    softly.assertThat(actual)
        .contains("abc");
  }

  /**
   * To method parameter stream.
   *
   * @throws Exception the exception
   */
  @Test
  void toMethodParameterStream() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"abc"});
    List<InvocationParameter> actual = invocation.toMethodParameterStream()
        .collect(Collectors.toList());
    List<InvocationParameter> expected = List
        .of(new InvocationParameter(invocation, method.getParameters()[0], "abc", 0));
    assertThat(actual)
        .isEqualTo(expected);
  }

  /**
   * Find annotation value on target class.
   *
   * @throws Exception the exception
   */
  @Test
  void findAnnotationValueOnTargetClass() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"123"});
    Optional<String> actual = invocation.findAnnotationValueOnTargetClass(
        RequestMapping.class,
        requestMapping -> requestMapping.path().length > 0,
        requestMapping -> requestMapping.path()[0]);
    assertThat(actual)
        .hasValue("/api");
  }

  /**
   * Find annotation value on method.
   *
   * @throws Exception the exception
   */
  @Test
  void findAnnotationValueOnMethod() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"123"});
    Optional<String> actual = invocation.findAnnotationValueOnMethod(
        GetMapping.class,
        getMapping -> getMapping.path().length > 0,
        getMapping -> getMapping.path()[0]);
    assertThat(actual)
        .hasValue("/example/{id}");
  }

  /**
   * Find annotation value on parameter.
   *
   * @throws Exception the exception
   */
  @Test
  void findAnnotationValueOnParameter() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"123"});
    Optional<String> actual = invocation.findAnnotationValueOnParameter(
        0,
        PathVariable.class,
        pathVariable -> !pathVariable.name().isEmpty(),
        PathVariable::name);
    assertThat(actual)
        .hasValue("id");
  }

  /**
   * Get.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
  @Test
  void get(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"abc"});
    softly.assertThat(invocation.getTargetClass())
        .isEqualTo(Example.class);
    softly.assertThat(invocation.getMethod())
        .isEqualTo(method);
    softly.assertThat(invocation.getArgs())
        .isEqualTo(new Object[]{"abc"});
  }

  /**
   * The interface Example.
   */
  @RequestMapping(path = "/api")
  interface Example {

    /**
     * Method a.
     *
     * @param id the id
     */
    @GetMapping(path = "/example/{id}")
    void methodA(@PathVariable(name = "id") String id);
  }
}