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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * The pageable request parameter resolver test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class PageableRequestParameterResolverTest {

  private static final PageableRequestParameterResolver target
      = new PageableRequestParameterResolver();

  /**
   * Apply.
   *
   * @param softly the softly
   * @throws Exception the exception
   */
  @Test
  void apply(SoftAssertions softly) throws Exception {
    Method method = Example.class.getMethod("methodA", Pageable.class);
    Pageable pageable = PageRequest.of(
        4, 25,
        Sort.by(Order.by("a"), Order.by("b").with(Direction.DESC)));
    Invocation invocation = new Invocation(Example.class, method, new Object[]{pageable});

    MultiValueMap<String, Object> expected = new LinkedMultiValueMap<>();
    expected.add("sort", "a");
    expected.add("sort", "b,desc");
    expected.add("page", 4);
    expected.add("size", 25);

    MultiValueMap<String, Object> actual = target.apply(invocation);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);

    expected = new LinkedMultiValueMap<>();
    expected.add("sort", "a");
    expected.add("sort", "b,desc");
    expected.add("p", 4);
    expected.add("z", 25);

    PageableRequestParameterResolver configuredTarget = target
        .withPageNumberRequestParamName("p")
        .withPageSizeRequestParamName("z");

    actual = configuredTarget.apply(invocation);
    softly.assertThat(actual)
        .containsExactlyInAnyOrderEntriesOf(expected);
  }

  /**
   * The interface Example.
   */
  @SuppressWarnings("unused")
  interface Example {

    /**
     * Method a.
     *
     * @param pageRequest the page request
     */
    void methodA(Pageable pageRequest);

    /**
     * Method b.
     *
     * @param pageRequest the page request
     */
    void methodB(@RequestBody Pageable pageRequest);

    /**
     * Method c.
     *
     * @param id the id
     */
    void methodC(String id);
  }
}