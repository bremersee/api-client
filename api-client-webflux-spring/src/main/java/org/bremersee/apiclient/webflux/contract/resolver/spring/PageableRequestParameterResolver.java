package org.bremersee.apiclient.webflux.contract.resolver.spring;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.resolver.AbstractInvocationParameterResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PageableRequestParameterResolver
    extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>> {

  private SortRequestParameterResolver sortRequestParameterResolver = new SortRequestParameterResolver();

  private String pageNumberRequestParamName = "page";

  private String pageSizeRequestParamName = "size";

  public PageableRequestParameterResolver withSortRequestParameterResolver(
      SortRequestParameterResolver sortRequestParameterResolver) {
    if (!isEmpty(sortRequestParameterResolver)) {
      this.sortRequestParameterResolver = sortRequestParameterResolver;
    }
    return this;
  }

  public PageableRequestParameterResolver withPageNumberRequestParamName(String pageNumberRequestParamName) {
    if (!isEmpty(pageNumberRequestParamName)) {
      this.pageNumberRequestParamName = pageNumberRequestParamName;
    }
    return this;
  }

  public PageableRequestParameterResolver withPageSizeRequestParamName(String pageSizeRequestParamName) {
    if (!isEmpty(pageSizeRequestParamName)) {
      this.pageSizeRequestParamName = pageSizeRequestParamName;
    }
    return this;
  }

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
