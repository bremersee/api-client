package org.bremersee.apiclient.webflux.contract.spring;

import java.util.function.Function;
import java.util.function.Predicate;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.util.MultiValueMap;

public interface RequestParametersResolverExtension extends
    Predicate<InvocationParameter>,
    Function<InvocationParameter, MultiValueMap<String, Object>> {

}
