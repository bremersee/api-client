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

import org.bremersee.apiclient.webflux.contract.spring.Extensions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

class ExtensionsTest {

  @Test
  void illegalExtensionsAnnotations() {
    assertThat(Extensions.ILLEGAL_EXTENSIONS_ANNOTATIONS)
        .contains(CookieValue.class,
            RequestBody.class,
            RequestHeader.class,
            RequestParam.class,
            PathVariable.class);
  }

  @Test
  void isSortPresent() {
    assertThat(Extensions.isSortPresent)
        .isTrue();
  }

  @Test
  void isPageablePresent() {
    assertThat(Extensions.isPageablePresent)
        .isTrue();
  }

}