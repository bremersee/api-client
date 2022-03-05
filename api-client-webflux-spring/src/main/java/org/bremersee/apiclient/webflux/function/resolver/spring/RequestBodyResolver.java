package org.bremersee.apiclient.webflux.function.resolver.spring;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bremersee.apiclient.webflux.function.Invocation;
import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.bremersee.exception.ServiceException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public class RequestBodyResolver implements Function<Invocation, Optional<InvocationParameter>> {

  private static final String ERROR_CODE = "org.bremersee:api-client:9b12803a-e54e-4d3a-85cd-5377d9f2355b";

  private Predicate<InvocationParameter> possibleBodyPredicate = new PossibleRequestBodyPredicate();

  public RequestBodyResolver withPossibleBodyPredicate(Predicate<InvocationParameter> possibleBodyPredicate) {
    if (nonNull(possibleBodyPredicate)) {
      this.possibleBodyPredicate = possibleBodyPredicate;
    }
    return this;
  }

  @Override
  public Optional<InvocationParameter> apply(Invocation invocation) {

    List<InvocationParameter> possibleBodies = invocation.toMethodParameterStream()
        .filter(possibleBodyPredicate)
        .sorted(new InvocationParameterComparator())
        .collect(Collectors.toList());
    if (possibleBodies.isEmpty()) {
      return Optional.empty();
    }
    InvocationParameter bodyParameter = possibleBodies.get(0);
    if (possibleBodies.size() == 1 || bodyParameter.hasParameterAnnotation(RequestBody.class)) {
      return Optional.of(bodyParameter);
    }
    throw ServiceException.internalServerError(
        "Resolving body failed. There are more than one parameters which are not annotated with @RequestBody.",
        ERROR_CODE);
  }

  public static class PossibleRequestBodyPredicate implements Predicate<InvocationParameter> {

    private final List<Predicate<InvocationParameter>> excludedBodies = new ArrayList<>();

    public PossibleRequestBodyPredicate add(Predicate<InvocationParameter> excludedRequestBody) {
      if (nonNull(excludedRequestBody)) {
        excludedBodies.add(excludedRequestBody);
      }
      return this;
    }

    @Override
    public boolean test(InvocationParameter invocationParameter) {
      if (invocationParameter.hasParameterAnnotation(RequestBody.class)) {
        return true;
      }
      if (invocationParameter.hasParameterAnnotation(PathVariable.class)) {
        return false;
      }
      if (invocationParameter.hasParameterAnnotation(RequestParam.class)) {
        return false;
      }
      if (invocationParameter.hasParameterAnnotation(RequestHeader.class)) {
        return false;
      }
      if (invocationParameter.hasParameterAnnotation(CookieValue.class)) {
        return false;
      }
      return excludedBodies.stream().noneMatch(excluded -> excluded.test(invocationParameter));
    }
  }

  protected static class InvocationParameterComparator implements Comparator<InvocationParameter> {

    @Override
    public int compare(InvocationParameter o1, InvocationParameter o2) {
      boolean a1 = o1.hasParameterAnnotation(RequestBody.class);
      boolean a2 = o2.hasParameterAnnotation(RequestBody.class);
      if (a1 && a2) {
        throw ServiceException.internalServerError(
            "Resolving body failed. There are more than one parameters which are annotated with @RequestBody.",
            "org.bremersee:api-client:47dbf2f1-1d89-43d2-8eab-16402477b8aa");
      }
      if (a1) {
        return -1;
      }
      if (a2) {
        return 1;
      }
      return Integer.compare(o1.getIndex(), o2.getIndex());
    }
  }

}
