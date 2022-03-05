package org.bremersee.apiclient.webflux.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class HeadersConsumerTest {

  @Test
  void accept() {
    //noinspection unchecked
    Function<Invocation, Optional<MediaType>> contentTypeResolver = mock(Function.class);
    when(contentTypeResolver.apply(any()))
        .thenReturn(Optional.of(MediaType.APPLICATION_JSON));

    //noinspection unchecked
    Function<Invocation, MediaType> acceptResolver = mock(Function.class);
    when(acceptResolver.apply(any()))
        .thenReturn(MediaType.ALL);

    //noinspection unchecked
    Function<Invocation, MultiValueMap<String, String>> headersResolver = mock(Function.class);
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Authorization", "b");
    when(headersResolver.apply(any()))
        .thenReturn(headers);

    HttpHeaders expected = new HttpHeaders();
    expected.setContentType(MediaType.APPLICATION_JSON);
    expected.setAccept(List.of(MediaType.ALL));
    expected.add("Authorization", "b");

    HeadersConsumer target = HeadersConsumer.builder()
        .contentTypeResolver(contentTypeResolver)
        .acceptResolver(acceptResolver)
        .headersResolver(headersResolver)
        .build();
    Invocation invocation = mock(Invocation.class);
    HttpHeaders actual = new HttpHeaders();
    target.accept(invocation, actual);
    assertThat(actual)
        .isEqualTo(expected);
  }
}