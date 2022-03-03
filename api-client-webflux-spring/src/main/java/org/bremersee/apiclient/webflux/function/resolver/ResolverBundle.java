package org.bremersee.apiclient.webflux.function.resolver;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.function.CookiesConsumer;
import org.bremersee.apiclient.webflux.function.FunctionBundle;
import org.bremersee.apiclient.webflux.function.HeadersConsumer;
import org.bremersee.apiclient.webflux.function.HttpRequestMethod;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.apiclient.webflux.function.RequestBodyInserterFunction;
import org.bremersee.apiclient.webflux.function.RequestUriFunction;
import org.bremersee.apiclient.webflux.function.RequestUriSpecFunction;
import org.bremersee.apiclient.webflux.function.ResponseFunction;
import org.immutables.value.Value;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

@Value.Immutable
@Valid
public interface ResolverBundle {

  static ImmutableResolverBundle.Builder builder(ResolverBundle resolverBundle) {
    Assert.notNull(resolverBundle, "Resolver bundle must be present.");
    return ImmutableResolverBundle.builder().from(resolverBundle);
  }

  @NotNull
  Function<Invocation, MediaType> getAcceptResolver();

  @NotNull
  Function<Invocation, Optional<MediaType>> getContentTypeResolver();

  @NotEmpty
  List<AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>> getCookieResolvers();

  @Value.Default
  @NotNull
  default AbstractInvocationResolver<String, MultiValueMap<String, String>> getCookiesResolver() {
    return new CookiesResolver(getCookieResolvers());
  }

  @NotNull
  Function<Invocation, HttpRequestMethod> getHttpMethodResolver();

  @NotEmpty
  List<AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>> getPathVariableResolvers();

  @Value.Default
  @NotNull
  default AbstractInvocationResolver<Object, MultiValueMap<String, Object>> getPathVariablesResolver() {
    return new PathVariablesResolver(getPathVariableResolvers());
  }

  @NotEmpty
  List<AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>> getRequestHeaderResolvers();

  @Value.Default
  @NotNull
  default AbstractInvocationResolver<String, MultiValueMap<String, String>> getRequestHeadersResolvers() {
    return new RequestHeadersResolver(getRequestHeaderResolvers());
  }

  @NotNull
  List<AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>> getRequestParameterResolvers();

  @Value.Default
  @NotEmpty
  default AbstractInvocationResolver<Object, MultiValueMap<String, Object>> getRequestParametersResolver() {
    return new RequestParametersResolver(getRequestParameterResolvers());
  }

  @NotNull
  Function<Invocation, String> getRequestPathResolver();

  @NotNull
  Function<Invocation, Optional<InvocationParameter>> getRequestBodyResolver();

  @Value.Derived
  @NotNull
  default FunctionBundle getFunctionBundle() {
    return FunctionBundle.builder()
        .cookiesConsumer(CookiesConsumer.builder()
            .cookiesResolver(getCookiesResolver())
            .build())
        .headersConsumer(HeadersConsumer.builder()
            .acceptResolver(getAcceptResolver())
            .contentTypeResolver(getContentTypeResolver())
            .headersResolver(getRequestHeadersResolvers())
            .build())
        .responseFunction(new ResponseFunction())
        .requestBodyInserterFunction(RequestBodyInserterFunction.builder()
            .requestBodyResolver(getRequestBodyResolver())
            .contentTypeResolver(getContentTypeResolver())
            .requestBodyResolver(getRequestBodyResolver())
            .build())
        .requestUriFunction(RequestUriFunction.builder()
            .pathVariablesResolver(getPathVariablesResolver())
            .requestParametersResolver(getRequestParametersResolver())
            .requestPathResolver(getRequestPathResolver())
            .build())
        .requestUriSpecFunction(RequestUriSpecFunction.builder()
            .httpMethodResolver(getHttpMethodResolver())
            .build())
        .build();
  }

}
