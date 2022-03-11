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
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.contract.HttpRequestMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The http method resolver test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class HttpMethodResolverTest {

  private static final HttpMethodResolver target = new HttpMethodResolver();

  /**
   * Apply.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
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
        .isInstanceOf(IllegalStateException.class);
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Method head.
     */
    @RequestMapping(method = RequestMethod.HEAD)
    void methodHead();

    /**
     * Method options.
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    void methodOptions();

    /**
     * Method b.
     */
    @GetMapping
    void methodB();

    /**
     * Method c.
     */
    @PostMapping
    void methodC();

    /**
     * Method d.
     */
    @PutMapping
    void methodD();

    /**
     * Method e.
     */
    @PatchMapping
    void methodE();

    /**
     * Method f.
     */
    @DeleteMapping
    void methodF();

    /**
     * Method g.
     */
    void methodG();

  }
}