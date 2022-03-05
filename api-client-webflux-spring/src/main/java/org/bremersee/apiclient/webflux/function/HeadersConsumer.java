package org.bremersee.apiclient.webflux.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Builder(toBuilder = true)
public class HeadersConsumer implements BiConsumer<Invocation, HttpHeaders> {

  @NonNull
  private Function<Invocation, Optional<MediaType>> contentTypeResolver;

  @NonNull
  private Function<Invocation, MediaType> acceptResolver;

  @NonNull
  private Function<Invocation, MultiValueMap<String, String>> headersResolver;

  @Override
  public void accept(Invocation invocation, HttpHeaders httpHeaders) {
    contentTypeResolver.apply(invocation)
        .ifPresent(httpHeaders::setContentType);
    List<MediaType> accepts = new ArrayList<>();
    accepts.add(acceptResolver.apply(invocation));
    httpHeaders.setAccept(accepts);
    httpHeaders.addAll(headersResolver.apply(invocation));
  }

}
