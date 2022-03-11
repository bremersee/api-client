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
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The path variables resolver test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class PathVariablesResolverTest {

  private static final PathVariablesResolver target = new PathVariablesResolver();

  /**
   * Apply.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
  @Test
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    Object value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, "nil"});
    Map<String, Object> actual = target.apply(invocation);
    softly.assertThat(actual)
        .isEqualTo(Map.of("id", value));
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Method a.
     *
     * @param id the id
     * @param name the name
     */
    void methodA(@PathVariable(name = "id") String id, @RequestParam(name = "name") String name);
  }
}