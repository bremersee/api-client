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

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * The extensions.
 *
 * @author Christian Bremer
 */
public abstract class Extensions {

  /**
   * The constant ILLEGAL_EXTENSIONS_ANNOTATIONS.
   */
  public static final Set<Class<? extends Annotation>> ILLEGAL_EXTENSIONS_ANNOTATIONS = Set.of(
      CookieValue.class,
      MatrixVariable.class,
      ModelAttribute.class,
      PathVariable.class,
      RequestAttribute.class,
      RequestBody.class,
      RequestHeader.class,
      RequestParam.class,
      RequestPart.class,
      SessionAttribute.class
  );

  /**
   * The constant isSortPresent.
   */
  public static final boolean isSortPresent;

  /**
   * The constant isPageablePresent.
   */
  public static final boolean isPageablePresent;

  static {
    isSortPresent = isPresent("org.springframework.data.domain.Sort");
    isPageablePresent = isPresent("org.springframework.data.domain.Pageable");
  }

  private Extensions() {
  }

  private static boolean isPresent(String clazz) {
    try {
      Class.forName(clazz);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

}
