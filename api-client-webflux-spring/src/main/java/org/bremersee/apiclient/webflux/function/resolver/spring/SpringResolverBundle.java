package org.bremersee.apiclient.webflux.function.resolver.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.function.HttpRequestMethod;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.apiclient.webflux.function.resolver.AbstractInvocationParameterResolver;
import org.bremersee.apiclient.webflux.function.resolver.ResolverBundle;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

public class SpringResolverBundle implements ResolverBundle {

  @Override
  public Function<Invocation, MediaType> getAcceptResolver() {
    return new AcceptResolver();
  }

  @Override
  public Function<Invocation, Optional<MediaType>> getContentTypeResolver() {
    return new ContentTypeResolver();
  }

  @Override
  public List<AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>> getCookieResolvers() {
    return List.of(new CookieResolver());
  }

  @Override
  public Function<Invocation, HttpRequestMethod> getHttpMethodResolver() {
    return new HttpMethodResolver();
  }

  @Override
  public List<AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>>
  getPathVariableResolvers() {

    return List.of(new PathVariableResolver());
  }

  @Override
  public List<AbstractInvocationParameterResolver<String, MultiValueMap<String, String>>>
  getRequestHeaderResolvers() {

    return List.of(new RequestHeaderResolver());
  }

  @Override
  public List<AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>>
  getRequestParameterResolvers() {

    List<AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>>> resolvers = new ArrayList<>();
    resolvers.add(new RequestParameterResolver());
    if (Extensions.isSortPresent) {
      resolvers.add(new SortRequestParameterResolver());
      if (Extensions.isPageablePresent) {
        resolvers.add(new PathVariableResolver());
      }
    }
    return resolvers;
  }

  @Override
  public Function<Invocation, String> getRequestPathResolver() {
    return new RequestPathResolver();
  }

  @Override
  public Function<Invocation, Optional<InvocationParameter>> getRequestBodyResolver() {
    return new RequestBodyResolver();
  }
}
