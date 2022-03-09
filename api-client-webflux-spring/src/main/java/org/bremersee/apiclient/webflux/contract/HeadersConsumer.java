package org.bremersee.apiclient.webflux.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface HeadersConsumer extends BiConsumer<Invocation, HttpHeaders> {

  static ImmutableHeadersConsumer.Builder builder() {
    return ImmutableHeadersConsumer.builder();
  }

  @NotNull
  Function<Invocation, Optional<MediaType>> getContentTypeResolver();

  @NotNull
  Function<Invocation, MediaType> getAcceptResolver();

  @NotNull
  Function<Invocation, MultiValueMap<String, String>> getHeadersResolver();

  @Override
  default void accept(Invocation invocation, HttpHeaders httpHeaders) {
    getContentTypeResolver().apply(invocation)
        .ifPresent(httpHeaders::setContentType);
    List<MediaType> accepts = new ArrayList<>();
    accepts.add(getAcceptResolver().apply(invocation));
    httpHeaders.setAccept(accepts);
    httpHeaders.addAll(getHeadersResolver().apply(invocation));
  }

}
