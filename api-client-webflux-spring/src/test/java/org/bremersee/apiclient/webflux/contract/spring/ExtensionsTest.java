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