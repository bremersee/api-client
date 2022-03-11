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

package org.bremersee.apiclient.webflux.contract;

import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.util.MultiValueMap;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface CookiesConsumer extends BiConsumer<Invocation, MultiValueMap<String, String>> {

  static ImmutableCookiesConsumer.Builder builder() {
    return ImmutableCookiesConsumer.builder();
  }

  @NotNull
  Function<Invocation, MultiValueMap<String, String>> getCookiesResolver();

  @Override
  default void accept(Invocation invocation, MultiValueMap<String, String> cookies) {
    cookies.addAll(getCookiesResolver().apply(invocation));
  }

}
