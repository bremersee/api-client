package org.bremersee.apiclient.webflux.contract.spring;

import java.util.List;
import java.util.function.BiFunction;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

@Value.Immutable
@Valid
public interface RequestBodyInserterRegistry extends
    BiFunction<Invocation, RequestBodyUriSpec, RequestHeadersUriSpec<?>> {

  static ImmutableRequestBodyInserterRegistry.Builder builder() {
    return ImmutableRequestBodyInserterRegistry.builder();
  }

  @NotEmpty
  List<RequestBodyInserter> getRequestBodyInserters();

  @Override
  default RequestHeadersUriSpec<?> apply(Invocation invocation, RequestBodyUriSpec requestBodyUriSpec) {
    //noinspection unchecked,rawtypes
    return getRequestBodyInserters().stream()
        .filter(inserter -> inserter.canInsert(invocation))
        .findFirst()
        .map(inserter -> inserter.apply(invocation, requestBodyUriSpec))
        .orElse((RequestHeadersUriSpec) requestBodyUriSpec);
  }
}
