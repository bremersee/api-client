package org.bremersee.apiclient.webflux.contract.resolver;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.bremersee.apiclient.webflux.contract.CookiesConsumer;
import org.bremersee.apiclient.webflux.contract.FunctionBundle;
import org.bremersee.apiclient.webflux.contract.HeadersConsumer;
import org.bremersee.apiclient.webflux.contract.HttpRequestMethod;
import org.bremersee.apiclient.webflux.contract.Invocation;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.RequestBodyInserterFunction;
import org.bremersee.apiclient.webflux.contract.RequestUriFunction;
import org.bremersee.apiclient.webflux.contract.RequestUriSpecFunction;
import org.bremersee.apiclient.webflux.contract.ResponseFunction;
import org.immutables.value.Value;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Value.Immutable
@Valid
public interface ResolverBundle {

  static ImmutableResolverBundle.Builder builder() {
    return ImmutableResolverBundle.builder();
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
