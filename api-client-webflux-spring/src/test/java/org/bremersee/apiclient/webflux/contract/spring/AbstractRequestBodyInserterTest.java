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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * The abstract request body inserter test.
 */
class AbstractRequestBodyInserterTest {

  /**
   * Can insert.
   *
   * @throws Exception the exception
   */
  @Test
  void canInsert() throws Exception {
    AbstractRequestBodyInserter target = mock(AbstractRequestBodyInserter.class);
    when(target.canInsert(any(Invocation.class)))
        .thenCallRealMethod();
    when(target.canInsert(anyList()))
        .thenCallRealMethod();
    when(target.findPossibleBodies(any(Invocation.class)))
        .thenCallRealMethod();
    when(target.isPossibleBody(any(InvocationParameter.class)))
        .thenCallRealMethod();
    when(target.hasMappingAnnotation(any(InvocationParameter.class)))
        .thenCallRealMethod();
    when(target.isPossibleBodyValue(any(InvocationParameter.class)))
        .thenReturn(true);
    Method method = Example.class.getMethod("methodA", String.class);
    Invocation invocation = new Invocation(Example.class, method, new Object[]{"text"});
    assertThat(target.canInsert(invocation))
        .isTrue();
  }

  /**
   * The interface Example.
   */
  interface Example {

    /**
     * Method a.
     *
     * @param body the body
     */
    void methodA(@RequestBody String body);
  }
}