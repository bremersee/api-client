package org.bremersee.apiclient.webflux.contract.spring;

import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.springframework.util.MultiValueMap;

public interface QueryParametersResolver extends Function<Invocation, MultiValueMap<String, Object>> {

}
