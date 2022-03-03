package org.bremersee.apiclient.webflux.function.resolver.spring;

import java.lang.annotation.Annotation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class Extensions {

  @SuppressWarnings("unchecked")
  public static final Class<? extends Annotation>[] ILLEGAL_EXTENSIONS_ANNOTATIONS = new Class[] {
      CookieValue.class,
      RequestBody.class,
      RequestHeader.class,
      RequestParam.class,
      PathVariable.class
  };

  public static final boolean isSortPresent;

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
