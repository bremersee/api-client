package org.bremersee.apiclient.webflux.contract.spring;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.web.bind.annotation.PathVariable;

public class PathVariablesResolver implements Function<Invocation, Map<String, Object>> {

  @Override
  public Map<String, Object> apply(Invocation invocation) {
    return invocation.toMethodParameterStream()
        .map(invocationParameter -> invocationParameter.toMultiValueMap(
            PathVariable.class,
            PathVariable::value,
            String::valueOf))
        .collect(
            LinkedHashMap::new,
            (a, b) -> a.putAll(b.toSingleValueMap()),
            LinkedHashMap::putAll);
  }
}
