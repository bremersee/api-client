package org.bremersee.apiclient.webflux.contract.spring;

import java.util.function.BiFunction;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public interface RequestBodyInserter extends
    BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> {

  boolean canInsert(Invocation invocation);

}
