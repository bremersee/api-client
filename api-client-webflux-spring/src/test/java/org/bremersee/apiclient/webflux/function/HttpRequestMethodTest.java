package org.bremersee.apiclient.webflux.function;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(SoftAssertionsExtension.class)
class HttpRequestMethodTest {

  @Test
  void invoke() {
    WebClient webClient = mock(WebClient.class);
    HttpRequestMethod.HEAD.invoke(webClient);
    verify(webClient).head();

    reset(webClient);
    HttpRequestMethod.DELETE.invoke(webClient);
    verify(webClient).delete();

    reset(webClient);
    HttpRequestMethod.GET.invoke(webClient);
    verify(webClient).get();

    reset(webClient);
    HttpRequestMethod.OPTIONS.invoke(webClient);
    verify(webClient).options();

    reset(webClient);
    HttpRequestMethod.PATCH.invoke(webClient);
    verify(webClient).patch();

    reset(webClient);
    HttpRequestMethod.POST.invoke(webClient);
    verify(webClient).post();

    reset(webClient);
    HttpRequestMethod.PUT.invoke(webClient);
    verify(webClient).put();
  }

  @Test
  void resolve(SoftAssertions softly) {
    softly.assertThat(HttpRequestMethod.resolve("HEAD"))
        .hasValue(HttpRequestMethod.HEAD);
    softly.assertThat(HttpRequestMethod.resolve("DELETE"))
        .hasValue(HttpRequestMethod.DELETE);
    softly.assertThat(HttpRequestMethod.resolve("GET"))
        .hasValue(HttpRequestMethod.GET);
    softly.assertThat(HttpRequestMethod.resolve("OPTIONS"))
        .hasValue(HttpRequestMethod.OPTIONS);
    softly.assertThat(HttpRequestMethod.resolve("PATCH"))
        .hasValue(HttpRequestMethod.PATCH);
    softly.assertThat(HttpRequestMethod.resolve("POST"))
        .hasValue(HttpRequestMethod.POST);
    softly.assertThat(HttpRequestMethod.resolve("PUT"))
        .hasValue(HttpRequestMethod.PUT);
    softly.assertThat(HttpRequestMethod.resolve("TRACE"))
        .isEmpty();
  }
}