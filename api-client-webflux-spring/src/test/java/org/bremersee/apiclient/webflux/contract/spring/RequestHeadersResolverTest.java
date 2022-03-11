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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.apiclient.webflux.Invocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@ExtendWith(SoftAssertionsExtension.class)
class RequestHeadersResolverTest {

  private static final RequestHeadersResolver target = new RequestHeadersResolver();

  @Test
  void apply() throws Exception {
    Method method = Example.class.getMethod("methodA", String.class, String.class);
    String value = "a";
    Invocation invocation = new Invocation(Example.class, method, new Object[]{value, null});
    MultiValueMap<String, String> actual = target.apply(invocation);
    assertThat(actual)
        .isEqualTo(Map.of("Authorization", List.of(value)));
  }

  interface Example {

    void methodA(@RequestHeader(name = "Authorization") String id, @RequestParam(name = "name") String name);
  }
}