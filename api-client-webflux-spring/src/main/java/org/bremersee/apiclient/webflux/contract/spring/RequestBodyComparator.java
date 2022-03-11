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

import java.util.Comparator;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

/**
 * The request body comparator.
 *
 * @author Christian Bremer
 */
class RequestBodyComparator implements Comparator<InvocationParameter> {

  @Override
  public int compare(InvocationParameter o1, InvocationParameter o2) {
    boolean a1 = o1.hasParameterAnnotation(RequestBody.class);
    boolean a2 = o2.hasParameterAnnotation(RequestBody.class);
    if (a1 && a2) {
      return 0;
    }
    if (a1) {
      return -1;
    }
    if (a2) {
      return 1;
    }
    a1 = o1.hasParameterAnnotation(RequestPart.class);
    a2 = o2.hasParameterAnnotation(RequestPart.class);
    if (a1 && a2) {
      return 0;
    }
    if (a1) {
      return -1;
    }
    if (a2) {
      return 1;
    }
    return Integer.compare(o1.getIndex(), o2.getIndex());
  }
}
