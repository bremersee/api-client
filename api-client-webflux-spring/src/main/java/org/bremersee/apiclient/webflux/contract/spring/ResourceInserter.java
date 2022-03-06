package org.bremersee.apiclient.webflux.contract.spring;

import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public class ResourceInserter extends SingleBodyInserter<Resource> {

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Resource;
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(
      Resource body,
      RequestBodyUriSpec requestBodyUriSpec) {

    //noinspection rawtypes
    return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters.fromResource(body));
  }

}
