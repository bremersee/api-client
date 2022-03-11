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
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.PathVariable;

@ExtendWith(SoftAssertionsExtension.class)
class InvocationTest {

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

  @Test
  void findAnnotationValue() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class);
    Optional<String> actual = Invocation.findAnnotationValue(
        method.getParameters()[0],
        PathVariable.class,
        pathVariable -> !pathVariable.name().isEmpty(),
        PathVariable::name);
    Assertions.assertThat(actual)
        .hasValue("id");
  }

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

  interface Example {

    void methodA(@PathVariable(name = "id") String id);
  }
}