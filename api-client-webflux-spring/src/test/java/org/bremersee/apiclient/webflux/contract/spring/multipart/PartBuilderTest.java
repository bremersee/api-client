package org.bremersee.apiclient.webflux.contract.spring.multipart;

import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SoftAssertionsExtension.class)
class PartBuilderTest {

  private static final PartBuilder target = new PartBuilder();

  private Path tmpFile;

  @AfterEach
  void deleteTmpFile() {
    if (nonNull(tmpFile)) {
      try {
        Files.delete(tmpFile);
        tmpFile = null;
      } catch (IOException e) {
        throw new IllegalStateException("Deleting tmp file failed.", e);
      }
    }
  }

  @Test
  void formFieldPart(SoftAssertions softly) {
    FormFieldPart actual = target.part("foo", "bar")
        .contentType(MediaType.TEXT_PLAIN)
        .withBufferSize(8)
        .withDataBufferFactory(new DefaultDataBufferFactory())
        .header("x-some", "thing")
        .headers(httpHeaders -> httpHeaders.add("x-else", "other"))
        .build();
    softly.assertThat(actual)
        .isNotNull()
        .extracting(Part::name)
        .isEqualTo("foo");
    softly.assertThat(actual.headers().getContentDisposition())
        .isNotNull()
        .extracting(ContentDisposition::getName)
        .isEqualTo("foo");
    softly.assertThat(actual)
        .isNotNull()
        .extracting(FormFieldPart::value)
        .isEqualTo("bar");
    softly.assertThat(actual)
        .isNotNull()
        .extracting(FormFieldPart::headers, InstanceOfAssertFactories.map(String.class, List.class))
        .containsKeys("x-some", "x-else");
    softly.assertThat(actual.headers().getContentType())
        .isEqualTo(MediaType.TEXT_PLAIN);
    softly.assertThat(actual.toString())
        .contains("foo");

    StepVerifier.create(DataBufferUtils.join(actual.content())
            .map(dataBuffer -> {
              byte[] bytes = new byte[dataBuffer.readableByteCount()];
              dataBuffer.read(bytes);
              DataBufferUtils.release(dataBuffer);
              return bytes;
            })
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8)))
        .assertNext(str -> softly.assertThat(str).isEqualTo("bar"))
        .verifyComplete();

    StepVerifier.create(actual.delete())
        .verifyComplete();
  }

  @Test
  void filePart(SoftAssertions softly) {
    String content = "Hello world!";
    try {
      tmpFile = Files.createTempFile("partbuilder", "java");
      Files.copy(
          new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
          tmpFile,
          StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      throw new IllegalStateException("Creating tmp file failed.", e);
    }

    Part actual = target.part("file", tmpFile)
        .withScheduler(Schedulers.boundedElastic())
        .filename("text.txt")
        .contentType(MediaType.TEXT_PLAIN)
        .build();

    softly.assertThat(actual)
        .isInstanceOf(FilePart.class);
    softly.assertThat(actual)
        .isNotNull()
        .extracting(Part::name)
        .isEqualTo("file");
    softly.assertThat(actual.headers().getContentDisposition())
        .isNotNull()
        .extracting(ContentDisposition::getName)
        .isEqualTo("file");
    softly.assertThat(actual.headers().getContentDisposition())
        .isNotNull()
        .extracting(ContentDisposition::getFilename)
        .isEqualTo("text.txt");
    softly.assertThat(actual.headers().getContentType())
        .isEqualTo(MediaType.TEXT_PLAIN);
    softly.assertThat(actual.toString())
        .contains("text.txt");

    StepVerifier.create(DataBufferUtils.join(actual.content())
            .map(dataBuffer -> {
              byte[] bytes = new byte[dataBuffer.readableByteCount()];
              dataBuffer.read(bytes);
              DataBufferUtils.release(dataBuffer);
              return bytes;
            })
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8)))
        .assertNext(str -> softly.assertThat(str).isEqualTo("Hello world!"))
        .verifyComplete();

    StepVerifier.create(((FilePart) actual).transferTo(tmpFile))
        .verifyComplete();
  }

  @Test
  void resourcePart(SoftAssertions softly) throws IOException {
    Part actual = target.part("file", new ClassPathResource("text.txt"))
        .filename("text.txt")
        .contentType(MediaType.TEXT_PLAIN)
        .build();

    softly.assertThat(actual)
        .isInstanceOf(FilePart.class);
    softly.assertThat(actual)
        .isNotNull()
        .extracting(Part::name)
        .isEqualTo("file");
    softly.assertThat(actual.headers().getContentDisposition())
        .isNotNull()
        .extracting(ContentDisposition::getName)
        .isEqualTo("file");
    softly.assertThat(actual.headers().getContentDisposition())
        .isNotNull()
        .extracting(ContentDisposition::getFilename)
        .isEqualTo("text.txt");
    softly.assertThat(actual.headers().getContentType())
        .isEqualTo(MediaType.TEXT_PLAIN);

    StepVerifier.create(DataBufferUtils.join(actual.content())
            .map(dataBuffer -> {
              byte[] bytes = new byte[dataBuffer.readableByteCount()];
              dataBuffer.read(bytes);
              DataBufferUtils.release(dataBuffer);
              return bytes;
            })
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8)))
        .assertNext(str -> softly.assertThat(str).isEqualTo("Hello world!"))
        .verifyComplete();

    StepVerifier.create(actual.delete())
        .verifyComplete();

    tmpFile = Files.createTempFile("partbuilder", "java");
    StepVerifier.create(((FilePart) actual).transferTo(tmpFile))
        .verifyComplete();
  }

  @Test
  void dataBufferPart(SoftAssertions softly) {
    Part actual = target.part("foo", target.part("tmp", "bar").build().content())
        .build();
    softly.assertThat(actual)
        .isNotNull()
        .extracting(Part::name)
        .isEqualTo("foo");
    softly.assertThat(actual)
        .isNotNull()
        .extracting(Object::getClass, InstanceOfAssertFactories.type(Class.class))
        .matches(cls -> !FilePart.class.isAssignableFrom(cls));
    softly.assertThat(actual.toString())
        .contains("foo");

    StepVerifier.create(DataBufferUtils.join(actual.content())
            .map(dataBuffer -> {
              byte[] bytes = new byte[dataBuffer.readableByteCount()];
              dataBuffer.read(bytes);
              DataBufferUtils.release(dataBuffer);
              return bytes;
            })
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8)))
        .assertNext(str -> softly.assertThat(str).isEqualTo("bar"))
        .verifyComplete();

    StepVerifier.create(actual.delete())
        .verifyComplete();
  }

  @Test
  void dataBufferPartWithFilename(SoftAssertions softly) {
    Part actual = target.part(
            "foo",
            "text.txt",
            target.part("tmp", "bar").build().content())
        .build();
    softly.assertThat(actual)
        .isNotNull()
        .extracting(Part::name)
        .isEqualTo("foo");
    softly.assertThat(actual.headers().getContentDisposition())
        .isNotNull()
        .extracting(ContentDisposition::getFilename)
        .isEqualTo("text.txt");
    softly.assertThat(actual)
        .isInstanceOf(FilePart.class);
  }

}