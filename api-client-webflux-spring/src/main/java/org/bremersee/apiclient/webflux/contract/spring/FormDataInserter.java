package org.bremersee.apiclient.webflux.contract.spring;

import static java.util.Objects.nonNull;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import org.bremersee.apiclient.webflux.Invocation;
import org.bremersee.apiclient.webflux.InvocationParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

public class FormDataInserter extends SingleBodyInserter<MultiValueMap<String, ?>> {

  private Function<Invocation, Optional<MediaType>> contentTypeResolver = new ContentTypeResolver();

  public FormDataInserter withContentTypeResolver(Function<Invocation, Optional<MediaType>> contentTypeResolver) {
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
  protected MultiValueMap<String, ?> mapBody(InvocationParameter invocationParameter) {
    //noinspection unchecked
    return (MultiValueMap<String, ?>) invocationParameter.getValue();
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
        .map(resolvableType -> resolvableType.resolveGeneric(0))
        .filter(String.class::isAssignableFrom)
        .isPresent();
  }

  @Override
  protected RequestHeadersUriSpec<?> insert(
      MultiValueMap<String, ?> body,
      RequestBodyUriSpec requestBodyUriSpec) {

    //noinspection rawtypes,unchecked
    return (RequestHeadersUriSpec) requestBodyUriSpec
        .body(BodyInserters.fromFormData((MultiValueMap<String, String>) body));
  }

}
