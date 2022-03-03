package org.bremersee.apiclient.webflux.function.resolver.spring;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.Setter;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.apiclient.webflux.function.resolver.AbstractInvocationParameterResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PageableRequestParameterResolver
    extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>> {

  @Setter
  @NonNull
  private SortRequestParameterResolver sortRequestParameterResolver = new SortRequestParameterResolver();

  @Setter
  @NonNull
  private String pageNumberRequestParamName = "page";

  @Setter
  @NonNull
  private String pageSizeRequestParamName = "size";

  @Override
  public boolean canResolve(InvocationParameter invocationParameter) {
    return (invocationParameter.getValue() instanceof Pageable)
        && invocationParameter.hasNoneParameterAnnotation(Extensions.ILLEGAL_EXTENSIONS_ANNOTATIONS);
  }

  @Override
  public MultiValueMap<String, Object> resolve(InvocationParameter invocationParameter) {
    return Optional.ofNullable(invocationParameter.getValue())
        .filter(value -> value instanceof Pageable)
        .map(value -> (Pageable) value)
        .map(this::getRequestParameters)

        .orElseGet(LinkedMultiValueMap::new);
  }

  protected MultiValueMap<String, Object> getRequestParameters(Pageable pageable) {
    MultiValueMap<String, Object> map = sortRequestParameterResolver.getRequestParameters(pageable.getSort());
    if (pageable.isPaged()) {
      map.put(pageNumberRequestParamName, List.of(pageable.getPageNumber()));
      map.put(pageSizeRequestParamName, List.of(pageable.getPageSize()));
    }
    return map;
  }
}
