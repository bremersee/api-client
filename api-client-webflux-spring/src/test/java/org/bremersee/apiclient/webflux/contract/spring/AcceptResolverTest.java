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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The accept resolver test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class AcceptResolverTest {

  private static final AcceptResolver target = new AcceptResolver();

  /**
   * Apply.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
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

  /**
   * Parse media type.
   *
   * @param softly the softly
   */
  @Test
  void parseMediaType(SoftAssertions softly) {
    softly.assertThat(target.parseMediaType("application/json"))
        .isEqualTo(MediaType.APPLICATION_JSON);

    softly.assertThat(target.parseMediaType("/json"))
        .isNull();
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Method a.
     */
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodA();

    /**
     * Method b.
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodB();

    /**
     * Method c.
     */
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodC();

    /**
     * Method d.
     */
    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void methodD();

    /**
     * Method e.
     */
    @PatchMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void methodE();

    /**
     * Method f.
     */
    @DeleteMapping
    void methodF();

  }
}