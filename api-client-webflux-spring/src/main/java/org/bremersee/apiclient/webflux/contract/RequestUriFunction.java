package org.bremersee.apiclient.webflux.contract;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.Invocation;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Valid
public interface RequestUriFunction extends BiFunction<Invocation, UriBuilder, URI> {

  static ImmutableRequestUriFunction.Builder builder() {
    return ImmutableRequestUriFunction.builder();
  }

  @NotNull
  Function<Invocation, String> getRequestPathResolver();

  @NotNull
  Function<Invocation, Map<String, Object>> getPathVariablesResolver();

  @NotEmpty
  List<Function<Invocation, MultiValueMap<String, Object>>> getRequestParametersResolvers();

  @Override
  default URI apply(Invocation invocation, UriBuilder uriBuilder) {
    UriBuilder builder = uriBuilder.path(getRequestPathResolver().apply(invocation));
    MultiValueMap<String, Object> requestParameters = getRequestParametersResolvers().stream()
        .map(resolver -> resolver.apply(invocation))
        .collect(
            LinkedMultiValueMap::new,
            LinkedMultiValueMap::putAll,
            LinkedMultiValueMap::putAll);
    for (Map.Entry<String, List<Object>> entry : requestParameters.entrySet()) {
      builder = builder.queryParam(entry.getKey(), entry.getValue());
    }
    return builder.build(getPathVariablesResolver().apply(invocation));
  }

}
