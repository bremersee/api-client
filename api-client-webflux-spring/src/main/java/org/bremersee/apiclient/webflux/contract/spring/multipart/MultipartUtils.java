package org.bremersee.apiclient.webflux.contract.spring.multipart;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Various static utility methods for dealing with multipart parsing.
 *
 * @author Arjen Poutsma
 */
abstract class MultipartUtils {

  /**
   * Return the character set of the given headers, as defined in the {@link
   * HttpHeaders#getContentType()} header.
   */
  public static Charset charset(HttpHeaders headers) {
    MediaType contentType = headers.getContentType();
    if (contentType != null) {
      Charset charset = contentType.getCharset();
      if (charset != null) {
        return charset;
      }
    }
    return StandardCharsets.UTF_8;
  }

}
