package org.bremersee.apiclient.webflux.contract.resolver.spring;

import static java.util.Objects.nonNull;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.util.Optional;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.resolver.AbstractInvocationParameterResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;

public class RequestHeaderResolver
    extends AbstractInvocationParameterResolver<String, MultiValueMap<String, String>> {

  @Override
  public boolean canResolve(InvocationParameter invocationParameter) {
    return nonNull(invocationParameter.getValue())
        && nonNull(findAnnotation(invocationParameter.getParameter(), RequestHeader.class));
  }

  @Override
  public MultiValueMap<String, String> resolve(InvocationParameter invocationParameter) {
    return Optional.of(invocationParameter)
        .filter(ip -> nonNull(ip.getValue()))
        .map(ip -> findAnnotation(ip.getParameter(), RequestHeader.class))
        .map(requestParam -> toMultiValueMap(
            invocationParameter,
            requestParam,
            param -> param.name().isBlank() ? param.value() : param.name(),
            String::valueOf))
        .orElseGet(LinkedMultiValueMap::new);
  }
}
