package org.bremersee.apiclient.webflux.contract.resolver;

import org.bremersee.apiclient.webflux.contract.InvocationParameter;
import org.springframework.util.MultiValueMap;

public interface InvocationParameterResolver<V, T extends MultiValueMap<String, V>> {

  boolean canResolve(InvocationParameter invocationParameter);

  T resolve(InvocationParameter invocationParameter);

}
