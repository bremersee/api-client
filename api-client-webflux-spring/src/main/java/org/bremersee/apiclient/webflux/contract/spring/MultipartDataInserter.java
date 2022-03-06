package org.bremersee.apiclient.webflux.contract.spring;

import static java.util.Objects.nonNull;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public class MultipartDataInserter extends SingleBodyInserter<MultiValueMap<String, String>> {

  private ContentTypeResolver contentTypeResolver = new ContentTypeResolver();

  public MultipartDataInserter withContentTypeResolver(ContentTypeResolver contentTypeResolver) {
    if (nonNull(contentTypeResolver)) {
      this.contentTypeResolver = contentTypeResolver;
    }
    return this;
  }

  @Override
  public boolean canInsert(Invocation invocation) {
    return isFormData(invocation) && super.canInsert(invocation);
  }

  protected boolean isFormData(Invocation invocation) {
    return contentTypeResolver.apply(invocation)
        .filter(contentType -> contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .isPresent();
  }

  @Override
  protected boolean isPossibleBodyValue(InvocationParameter invocationParameter) {
    return invocationParameter.getValue() instanceof MultiValueMap && isString(invocationParameter);
  }

  private boolean isString(InvocationParameter invocationParameter) {
    Method method = invocationParameter.getMethod();
    int index = invocationParameter.getIndex();
    return Optional.of(ResolvableType.forMethodParameter(method, index))
        .filter(resolvableType -> resolvableType.getGenerics().length >= 2)
        .filter(resolvableType -> {
          Class<?> resolvedGeneric0 = resolvableType.resolveGeneric(0);
          Class<?> resolvedGeneric1 = resolvableType.resolveGeneric(1);
          return nonNull(resolvedGeneric0)
              && nonNull(resolvedGeneric1)
              && String.class.isAssignableFrom(resolvedGeneric0)
              && String.class.isAssignableFrom(resolvedGeneric1);
        })
        .isPresent();
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(
      MultiValueMap<String, String> body,
      RequestBodyUriSpec requestBodyUriSpec) {

    /*
    Publisher<DataBuffer> dataBuffer = null;
    requestBodyUriSpec.body(BodyInserters.fromDataBuffers(dataBuffer));

     */
    MultiValueMap<String, Objects> m = new LinkedMultiValueMap<>();

    Publisher<?> p;

    // requestBodyUriSpec.body(BodyInserters.fromPublisher());

    //noinspection rawtypes
    return (RequestHeadersUriSpec) requestBodyUriSpec.body(BodyInserters.fromFormData(body));
  }

}
