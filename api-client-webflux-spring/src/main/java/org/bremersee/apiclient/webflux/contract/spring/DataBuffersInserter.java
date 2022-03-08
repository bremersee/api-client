package org.bremersee.apiclient.webflux.contract.spring;

import java.lang.reflect.Method;
import java.util.Optional;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public class DataBuffersInserter extends SingleBodyInserter<Publisher<DataBuffer>> {

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof Publisher
        && isDataBuffer(invocationParameter);
  }

  private boolean isDataBuffer(InvocationParameter invocationParameter) {
    Method method = invocationParameter.getMethod();
    int index = invocationParameter.getIndex();
    return Optional.of(ResolvableType.forMethodParameter(method, index))
        .filter(ResolvableType::hasGenerics)
        .map(resolvableType -> resolvableType.resolveGeneric(0))
        .filter(DataBuffer.class::isAssignableFrom)
        .isPresent();
  }

  @Override
  protected Publisher<DataBuffer> mapBody(InvocationParameter invocationParameter) {
    //noinspection unchecked
    return (Publisher<DataBuffer>) invocationParameter.getValue();
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(Publisher<DataBuffer> body, RequestBodyUriSpec requestBodyUriSpec) {
    requestBodyUriSpec.body(BodyInserters.fromDataBuffers(body));
    return requestBodyUriSpec;
  }

}
