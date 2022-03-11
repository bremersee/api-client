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

package org.bremersee.apiclient.webflux.contract.spring;

import java.lang.reflect.Method;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The request path resolver test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class RequestPathResolverTest {

  private static final RequestPathResolver target = new RequestPathResolver();

  /**
   * Apply.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
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

  /**
   * The interface Example a.
   */
  @RequestMapping(value = "/api")
  interface ExampleA {

    /**
     * Method a.
     */
    @RequestMapping(value = "/a")
    void methodA();

    /**
     * Method b.
     */
    @GetMapping(value = "/b")
    void methodB();

    /**
     * Method c.
     */
    @PostMapping(value = "/c")
    void methodC();

    /**
     * Method d.
     */
    @PutMapping(value = "/d")
    void methodD();

    /**
     * Method e.
     */
    @PatchMapping(value = "/e")
    void methodE();

    /**
     * Method f.
     */
    @DeleteMapping(value = "/f")
    void methodF();
  }

  /**
   * The interface Example b.
   */
  @RequestMapping(path = "/api")
  interface ExampleB {

    /**
     * Method a.
     */
    @RequestMapping(path = "/a")
    void methodA();

    /**
     * Method b.
     */
    @GetMapping(path = "/b")
    void methodB();

    /**
     * Method c.
     */
    @PostMapping(path = "/c")
    void methodC();

    /**
     * Method d.
     */
    @PutMapping(path = "/d")
    void methodD();

    /**
     * Method e.
     */
    @PatchMapping(path = "/e")
    void methodE();

    /**
     * Method f.
     */
    @DeleteMapping(path = "/f")
    void methodF();
  }
}