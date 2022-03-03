package org.bremersee.apiclient.webflux.function;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;

@Builder(toBuilder = true)
public class RequestUriFunction implements BiFunction<Invocation, UriBuilder, URI> {

  @NonNull
  private Function<Invocation, String> requestPathResolver;

  @NonNull
  private Function<Invocation, MultiValueMap<String, Object>> pathVariablesResolver;

  @NonNull
  private Function<Invocation, MultiValueMap<String, Object>> requestParametersResolver;

  @Override
  public URI apply(Invocation invocation, UriBuilder uriBuilder) {
    UriBuilder builder = uriBuilder.path(requestPathResolver.apply(invocation));
    MultiValueMap<String, Object> requestParameters = requestParametersResolver.apply(invocation);
    for (Map.Entry<String, List<Object>> entry : requestParameters.entrySet()) {
      builder = builder.queryParam(entry.getKey(), entry.getValue());
    }
    return builder.build(pathVariablesResolver.apply(invocation).toSingleValueMap());
  }

}
