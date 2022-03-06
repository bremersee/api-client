package org.bremersee.apiclient.webflux.contract.spring;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.List;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PageableRequestParameterResolver extends SortRequestParameterResolver {

  private String pageNumberRequestParamName = "page";

  private String pageSizeRequestParamName = "size";

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
  public MultiValueMap<String, Object> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .filter(this)
        .map(InvocationParameter::getValue)
        .map(value -> getRequestParameters((Pageable) value))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::addAll,
            LinkedMultiValueMap::addAll);
  }

  @Override
  public boolean test(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Pageable
        && invocationParameter.hasNoneParameterAnnotation(Extensions.ILLEGAL_EXTENSIONS_ANNOTATIONS);
  }

  protected MultiValueMap<String, Object> getRequestParameters(Pageable pageable) {
    MultiValueMap<String, Object> map = super.getRequestParameters(pageable.getSort());
    if (pageable.isPaged()) {
      map.put(pageNumberRequestParamName, List.of(pageable.getPageNumber()));
      map.put(pageSizeRequestParamName, List.of(pageable.getPageSize()));
    }
    return map;
  }
}
