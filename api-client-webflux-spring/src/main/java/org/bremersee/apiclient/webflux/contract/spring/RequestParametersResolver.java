package org.bremersee.apiclient.webflux.contract.spring;

import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

public class RequestParametersResolver implements QueryParametersResolver {

  @Override
  public MultiValueMap<String, Object> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .map(invocationParameter -> invocationParameter.toMultiValueMap(
            RequestParam.class,
            RequestParam::value,
            v -> v))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::putAll,
            LinkedMultiValueMap::putAll);
  }
}
