package org.bremersee.apiclient.webflux.function;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import org.bremersee.exception.ServiceException;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

@Builder(toBuilder = true)
public class RequestUriSpecFunction
    implements BiFunction<Invocation, WebClient, RequestHeadersUriSpec<?>> {

  private static final String ERROR_CODE = "org.bremersee:api-client:5222f2b4-1810-41bf-acfc-37988571304b";

  @NonNull
  private Function<Invocation, HttpRequestMethod> httpMethodResolver;

  @Override
  public RequestHeadersUriSpec<?> apply(Invocation invocation, WebClient webClient) {

    Assert.notNull(invocation, "Invocation must be present.");
    Assert.notNull(webClient, "Web client must be present.");
    return Optional.ofNullable(httpMethodResolver.apply(invocation))
        .map(httpRequestMethod -> httpRequestMethod.invoke(webClient))
        .orElseThrow(() -> ServiceException.internalServerError(
            String.format("Cannot find request method on method '%s'.", invocation.getMethod().getName()),
            ERROR_CODE));
  }

}
