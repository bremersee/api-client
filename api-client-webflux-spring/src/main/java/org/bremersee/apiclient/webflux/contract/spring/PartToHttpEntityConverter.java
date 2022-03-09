package org.bremersee.apiclient.webflux.contract.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;

public class PartToHttpEntityConverter implements Converter<Part, HttpEntity<?>> {

  private final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

  @Override
  public HttpEntity<?> convert(Part source) {
    multipartBodyBuilder.part(source.name(), source);
    return multipartBodyBuilder.build().getFirst(source.name());
  }
}
