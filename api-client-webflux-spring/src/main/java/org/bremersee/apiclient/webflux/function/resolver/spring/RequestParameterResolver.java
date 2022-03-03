package org.bremersee.apiclient.webflux.function.resolver.spring;

import static java.util.Objects.nonNull;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.util.Optional;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.apiclient.webflux.function.resolver.AbstractInvocationParameterResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

public class RequestParameterResolver
    extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>> {

  @Override
  public boolean canResolve(InvocationParameter invocationParameter) {
    return nonNull(invocationParameter.getValue())
        && nonNull(findAnnotation(invocationParameter.getParameter(), RequestParam.class));
  }

  @Override
  public MultiValueMap<String, Object> resolve(InvocationParameter invocationParameter) {
    return Optional.of(invocationParameter)
        .filter(ip -> nonNull(ip.getValue()))
        .map(ip -> findAnnotation(ip.getParameter(), RequestParam.class))
        .map(requestParam -> toMultiValueMap(
            invocationParameter,
            requestParam,
            param -> param.name().isBlank() ? param.value() : param.name(),
            v -> v))
        .orElseGet(LinkedMultiValueMap::new);
  }
}
