package org.bremersee.apiclient.webflux.function.resolver;

import org.bremersee.apiclient.webflux.function.InvocationParameter;
import org.springframework.util.MultiValueMap;

public interface InvocationParameterResolver<V, T extends MultiValueMap<String, V>> {

  boolean canResolve(InvocationParameter invocationParameter);

  T resolve(InvocationParameter invocationParameter);

}
