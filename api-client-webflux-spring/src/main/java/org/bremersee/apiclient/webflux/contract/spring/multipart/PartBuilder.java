package org.bremersee.apiclient.webflux.contract.spring.multipart;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public abstract class PartBuilder<T extends Part> {

  private DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

  private int bufferSize = 1024;

  private final HttpHeaders headers = new HttpHeaders();

  protected PartBuilder() {
  }

  // TODO statt static part builder nur mit build methoden, die dann den eigentlichen builder zurück geben
  public static FormFieldPartBuilder part(String name, String value) {
    return new FormFieldPartBuilder(name, value);
  }

  public static FilePartBuilder part(String name, Path file) {
    return new FilePartBuilder(name, file);
  }

  public static ResourcePartBuilder part(String name, Resource resource) {
    return new ResourcePartBuilder(name, resource);
  }

  public static DataBufferPartBuilder part(String name, Flux<DataBuffer> content) {
    return new DataBufferPartBuilder(name, content);
  }

  public static DataBufferPartBuilder part(String name, String filename, Flux<DataBuffer> content) {
    return new DataBufferPartBuilder(name, filename, content);
  }

  protected DataBufferFactory getDataBufferFactory() {
    return dataBufferFactory;
  }

  protected int getBufferSize() {
    return bufferSize;
  }

  protected HttpHeaders getHeaders() {
    return headers;
  }

  public PartBuilder<T> withDataBufferFactory(DataBufferFactory dataBufferFactory) {
    if (!isEmpty(dataBufferFactory)) {
      this.dataBufferFactory = dataBufferFactory;
    }
    return this;
  }

  public PartBuilder<T> withBufferSize(int bufferSize) {
    if (bufferSize > 0) {
      this.bufferSize = bufferSize;
    }
    return this;
  }

  public PartBuilder<T> contentType(MediaType contentType) {
    if (!isEmpty(contentType)) {
      headers.setContentType(contentType);
    }
    return this;
  }

  public PartBuilder<T> header(String headerName, String... headerValues) {
    if (!isEmpty(headerName) && !isEmpty(headerValues)) {
      headers.addAll(headerName, Arrays.asList(headerValues));
    }
    return this;
  }

  public PartBuilder<T> headers(Consumer<HttpHeaders> headersConsumer) {
    if (!isEmpty(headersConsumer)) {
      headersConsumer.accept(headers);
    }
    return this;
  }

  public abstract T build();

  public static class FormFieldPartBuilder extends PartBuilder<FormFieldPart> {

    private final String value;

    public FormFieldPartBuilder(String name, String value) {
      getHeaders().setContentDispositionFormData(name, null);
      this.value = value;
    }

    @Override
    public FormFieldPart build() {
      return DefaultParts.formFieldPart(getHeaders(), value);
    }
  }

  public static abstract class AbstractPartBuilder extends PartBuilder<Part> {

    public AbstractPartBuilder filename(String filename) {
      if (!isEmpty(filename) && !isEmpty(getHeaders().getContentDisposition())) {
        String name = getHeaders().getContentDisposition().getName();
        if (!isEmpty(name)) {
          getHeaders().setContentDispositionFormData(name, filename);
        }
      }
      return this;
    }

  }

  public static class FilePartBuilder extends AbstractPartBuilder {

    private Scheduler blockingOperationScheduler = Schedulers.boundedElastic();

    private final Path file;

    public FilePartBuilder(String name, Path file) {
      this.file = file;
      getHeaders().setContentDispositionFormData(name, String.valueOf(file.getFileName()));
    }

    public FilePartBuilder withScheduler(Scheduler scheduler) {
      if (!isEmpty(scheduler)) {
        this.blockingOperationScheduler = scheduler;
      }
      return this;
    }

    @Override
    public Part build() {
      return DefaultParts.part(getHeaders(), file, blockingOperationScheduler);
    }
  }

  public static class ResourcePartBuilder extends AbstractPartBuilder {

    private final Resource resource;

    public ResourcePartBuilder(String name, Resource resource) {
      this.resource = resource;
      getHeaders().setContentDispositionFormData(name, resource.getFilename());
    }

    @Override
    public Part build() {
      return DefaultParts.part(getHeaders(), DataBufferUtils.read(resource, getDataBufferFactory(), getBufferSize()));
    }
  }

  public static class DataBufferPartBuilder extends AbstractPartBuilder {

    private final Flux<DataBuffer> content;

    public DataBufferPartBuilder(String name, Flux<DataBuffer> content) {
      this(name, null, content);
    }

    public DataBufferPartBuilder(String name, String filename, Flux<DataBuffer> content) {
      this.content = content;
      getHeaders().setContentDispositionFormData(name, filename);
    }

    @Override
    public Part build() {
      return DefaultParts.part(getHeaders(), content);
    }
  }

}
