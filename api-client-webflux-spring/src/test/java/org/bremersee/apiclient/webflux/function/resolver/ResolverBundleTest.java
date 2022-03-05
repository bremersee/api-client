package org.bremersee.apiclient.webflux.function.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.function.FunctionBundle;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

class ResolverBundleTest {

  @Test
  void getFunctionBundle() {
    //noinspection unchecked
    ResolverBundle target = ResolverBundle.builder()
        .acceptResolver(mock(Function.class))
        .contentTypeResolver(mock(Function.class))
        .cookieResolvers(List.of(
            (AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>) mock(
                AbstractInvocationParameterResolver.class)))
        .httpMethodResolver(mock(Function.class))
        .pathVariableResolvers(List.of(
            (AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>) mock(
                AbstractInvocationParameterResolver.class)))
        .requestHeaderResolvers(List.of(
            (AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>) mock(
                AbstractInvocationParameterResolver.class)))
        .requestParameterResolvers(List.of(
            (AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>) mock(
                AbstractInvocationParameterResolver.class)))
        .requestPathResolver(mock(Function.class))
        .requestBodyResolver(mock(Function.class))
        .build();
    FunctionBundle actual = target.getFunctionBundle();
    assertThat(actual)
        .isNotNull();
  }
}