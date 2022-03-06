package org.bremersee.apiclient.webflux.contract.spring;

import static java.util.Objects.nonNull;

import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public class ValueInserter extends SingleBodyInserter<Object> {

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return nonNull(invocationParameter.getValue());
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(Object body, RequestBodyUriSpec requestBodyUriSpec) {
    //noinspection rawtypes
    return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters.fromValue(body));
  }

}
