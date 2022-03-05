package org.bremersee.apiclient.webflux.function.resolver.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SpringResolverBundleTest {

  private static final SpringResolverBundle target = new SpringResolverBundle();

  @Test
  void getAcceptResolver() {
    assertThat(target.getAcceptResolver())
        .isNotNull();
  }

  @Test
  void getContentTypeResolver() {
    assertThat(target.getContentTypeResolver())
        .isNotNull();
  }

  @Test
  void getCookieResolvers() {
    assertThat(target.getCookieResolvers())
        .isNotEmpty();
  }

  @Test
  void getHttpMethodResolver() {
    assertThat(target.getHttpMethodResolver())
        .isNotNull();
  }

  @Test
  void getPathVariableResolvers() {
    assertThat(target.getPathVariableResolvers())
        .isNotEmpty();
  }

  @Test
  void getRequestHeaderResolvers() {
    assertThat(target.getRequestHeaderResolvers())
        .isNotEmpty();
  }

  @Test
  void getRequestParameterResolvers() {
    assertThat(target.getRequestParameterResolvers())
        .isNotEmpty();
  }

  @Test
  void getRequestPathResolver() {
    assertThat(target.getRequestPathResolver())
        .isNotNull();
  }

  @Test
  void getRequestBodyResolver() {
    assertThat(target.getRequestBodyResolver())
        .isNotNull();
  }
}