package org.bremersee.apiclient.webflux.contract.spring;

import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;

public class RequestHeadersResolver implements
    Function<Invocation, MultiValueMap<String, String>> {

  @Override
  public MultiValueMap<String, String> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .map(invocationParameter -> invocationParameter.toMultiValueMap(
            RequestHeader.class,
            RequestHeader::value,
            String::valueOf))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::addAll,
            LinkedMultiValueMap::addAll);
  }

}
