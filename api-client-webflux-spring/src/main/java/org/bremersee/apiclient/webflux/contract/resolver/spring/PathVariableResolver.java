package org.bremersee.apiclient.webflux.contract.resolver.spring;

import static java.util.Objects.nonNull;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.util.Optional;
import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.bremersee.apiclient.webflux.contract.resolver.AbstractInvocationParameterResolver;
import org.bremersee.exception.ServiceException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;

public class PathVariableResolver
    extends AbstractInvocationParameterResolver<Object, MultiValueMap<String, Object>> {

  private static final String ERROR_CODE = "org.bremersee:api-client:be4886af-d9e4-4cc1-be2b-dac037437840";

  @Override
  public boolean canResolve(InvocationParameter invocationParameter) {
    return nonNull(findAnnotation(invocationParameter.getParameter(), PathVariable.class));
  }

  @Override
  public MultiValueMap<String, Object> resolve(InvocationParameter invocationParameter) {
    return Optional.of(invocationParameter)
        .filter(ip -> nonNull(ip.getValue()))
        .map(ip -> findAnnotation(ip.getParameter(), PathVariable.class))
        .map(requestParam -> toMultiValueMap(
            invocationParameter,
            requestParam,
            param -> param.name().isBlank() ? param.value() : param.name(),
            v -> v))
        .orElseThrow(() -> ServiceException.internalServerError(
            String.format("A path variable is missing for %s", invocationParameter),
            ERROR_CODE));
  }
}
