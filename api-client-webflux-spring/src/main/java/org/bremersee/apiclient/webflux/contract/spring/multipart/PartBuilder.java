/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * The part builder.
 *
 * @author Christian Bremer
 */
public class PartBuilder {

  /**
   * Instantiates a new part builder.
   */
  public PartBuilder() {
  }

  /**
   * Form field part builder.
   *
   * @param name the name
   * @param value the value
   * @return the form field part builder
   */
  public FormFieldPartBuilder part(String name, String value) {
    return new FormFieldPartBuilder(name, value);
  }

  /**
   * File part builder.
   *
   * @param name the name
   * @param file the file
   * @return the file part builder
   */
  public FilePartBuilder part(String name, Path file) {
    return new FilePartBuilder(name, file);
  }

  /**
   * Resource part builder.
   *
   * @param name the name
   * @param resource the resource
   * @return the resource part builder
   */
  public ResourcePartBuilder part(String name, Resource resource) {
    return new ResourcePartBuilder(name, resource);
  }

  /**
   * Data buffer part builder.
   *
   * @param name the name
   * @param content the content
   * @return the data buffer part builder
   */
  public DataBufferPartBuilder part(String name, Flux<DataBuffer> content) {
    return new DataBufferPartBuilder(name, content);
  }

  /**
   * Data buffer part builder.
   *
   * @param name the name
   * @param filename the filename
   * @param content the content
   * @return the data buffer part builder
   */
  public DataBufferPartBuilder part(String name, String filename, Flux<DataBuffer> content) {
    return new DataBufferPartBuilder(name, filename, content);
  }

  /**
   * The abstract part builder.
   *
   * @param <T> the type parameter
   */
  public abstract static class AbstractPartBuilder<T extends Part> {

    private DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    private int bufferSize = 1024;

    private final HttpHeaders headers = new HttpHeaders();

    /**
     * Instantiates a new abstract part builder.
     */
    AbstractPartBuilder() {
    }

    /**
     * Gets data buffer factory.
     *
     * @return the data buffer factory
     */
    protected DataBufferFactory getDataBufferFactory() {
      return dataBufferFactory;
    }

    /**
     * Gets buffer size.
     *
     * @return the buffer size
     */
    protected int getBufferSize() {
      return bufferSize;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     */
    protected HttpHeaders getHeaders() {
      return headers;
    }

    /**
     * With data buffer factory.
     *
     * @param dataBufferFactory the data buffer factory
     * @return the abstract part builder
     */
    public AbstractPartBuilder<T> withDataBufferFactory(DataBufferFactory dataBufferFactory) {
      if (!isEmpty(dataBufferFactory)) {
        this.dataBufferFactory = dataBufferFactory;
      }
      return this;
    }

    /**
     * With buffer size.
     *
     * @param bufferSize the buffer size
     * @return the abstract part builder
     */
    public AbstractPartBuilder<T> withBufferSize(int bufferSize) {
      if (bufferSize > 0) {
        this.bufferSize = bufferSize;
      }
      return this;
    }

    /**
     * Content type.
     *
     * @param contentType the content type
     * @return the abstract part builder
     */
    public AbstractPartBuilder<T> contentType(MediaType contentType) {
      if (!isEmpty(contentType)) {
        headers.setContentType(contentType);
      }
      return this;
    }

    /**
     * Header.
     *
     * @param headerName the header name
     * @param headerValues the header values
     * @return the abstract part builder
     */
    public AbstractPartBuilder<T> header(String headerName, String... headerValues) {
      if (!isEmpty(headerName) && !isEmpty(headerValues)) {
        headers.addAll(headerName, Arrays.asList(headerValues));
      }
      return this;
    }

    /**
     * Headers.
     *
     * @param headersConsumer the headers consumer
     * @return the abstract part builder
     */
    public AbstractPartBuilder<T> headers(Consumer<HttpHeaders> headersConsumer) {
      if (!isEmpty(headersConsumer)) {
        headersConsumer.accept(headers);
      }
      return this;
    }

    /**
     * Build part.
     *
     * @return the part
     */
    public abstract T build();
  }

  /**
   * The form field part builder.
   */
  public static class FormFieldPartBuilder extends AbstractPartBuilder<FormFieldPart> {

    private final String value;

    /**
     * Instantiates a new form field part builder.
     *
     * @param name the name
     * @param value the value
     */
    FormFieldPartBuilder(String name, String value) {
      Assert.hasText(name, "Name must be present.");
      getHeaders().setContentDispositionFormData(name, null);
      this.value = value;
    }

    @Override
    public FormFieldPart build() {
      return DefaultParts.formFieldPart(getHeaders(), value);
    }
  }

  /**
   * The abstract file part builder.
   */
  public abstract static class AbstractFilePartBuilder extends AbstractPartBuilder<Part> {

    /**
     * Instantiates a new abstract file part builder.
     */
    AbstractFilePartBuilder() {
    }

    /**
     * Filename abstract file part builder.
     *
     * @param filename the filename
     * @return the abstract file part builder
     */
    public AbstractFilePartBuilder filename(String filename) {
      if (!isEmpty(filename) && !isEmpty(getHeaders().getContentDisposition())) {
        String name = getHeaders().getContentDisposition().getName();
        if (!isEmpty(name)) {
          getHeaders().setContentDispositionFormData(name, filename);
        }
      }
      return this;
    }
  }

  /**
   * The file part builder.
   */
  public static class FilePartBuilder extends AbstractFilePartBuilder {

    private Scheduler blockingOperationScheduler = Schedulers.boundedElastic();

    private final Path file;

    /**
     * Instantiates a new file part builder.
     *
     * @param name the name
     * @param file the file
     */
    FilePartBuilder(String name, Path file) {
      Assert.hasText(name, "Name must be present.");
      Assert.notNull(file, "File must be present.");
      this.file = file;
      getHeaders().setContentDispositionFormData(name, String.valueOf(file.getFileName()));
    }

    /**
     * With scheduler.
     *
     * @param scheduler the scheduler
     * @return the file part builder
     */
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

  /**
   * The resource part builder.
   */
  public static class ResourcePartBuilder extends AbstractFilePartBuilder {

    private final Resource resource;

    /**
     * Instantiates a new resource part builder.
     *
     * @param name the name
     * @param resource the resource
     */
    ResourcePartBuilder(String name, Resource resource) {
      Assert.hasText(name, "Name must be present.");
      Assert.notNull(resource, "Resource must be present.");
      this.resource = resource;
      getHeaders().setContentDispositionFormData(name, resource.getFilename());
    }

    @Override
    public Part build() {
      return DefaultParts.part(getHeaders(),
          DataBufferUtils.read(resource, getDataBufferFactory(), getBufferSize()));
    }
  }

  /**
   * The data buffer part builder.
   */
  public static class DataBufferPartBuilder extends AbstractFilePartBuilder {

    private final Flux<DataBuffer> content;

    /**
     * Instantiates a new data buffer part builder.
     *
     * @param name the name
     * @param content the content
     */
    DataBufferPartBuilder(String name, Flux<DataBuffer> content) {
      this(name, null, content);
    }

    /**
     * Instantiates a new data buffer part builder.
     *
     * @param name the name
     * @param filename the filename
     * @param content the content
     */
    DataBufferPartBuilder(String name, String filename, Flux<DataBuffer> content) {
      Assert.hasText(name, "Name must be present.");
      Assert.notNull(content, "Content must be present.");
      this.content = content;
      getHeaders().setContentDispositionFormData(name, filename);
    }

    @Override
    public Part build() {
      return DefaultParts.part(getHeaders(), content);
    }
  }

}
