package org.bremersee.apiclient.webflux.contract.spring.multipart;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Various static utility methods for dealing with multipart parsing.
 * @author Arjen Poutsma
 */
abstract class MultipartUtils {

  /**
   * Return the character set of the given headers, as defined in the
   * {@link HttpHeaders#getContentType()} header.
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

  /**
   * Concatenates the given array of byte arrays.
   */
  public static byte[] concat(byte[]... byteArrays) {
    int len = 0;
    for (byte[] byteArray : byteArrays) {
      len += byteArray.length;
    }
    byte[] result = new byte[len];
    len = 0;
    for (byte[] byteArray : byteArrays) {
      System.arraycopy(byteArray, 0, result, len, byteArray.length);
      len += byteArray.length;
    }
    return result;
  }

  /**
   * Slices the given buffer to the given index (exclusive).
   */
  public static DataBuffer sliceTo(DataBuffer buf, int idx) {
    int pos = buf.readPosition();
    int len = idx - pos + 1;
    return buf.retainedSlice(pos, len);
  }

  /**
   * Slices the given buffer from the given index (inclusive).
   */
  public static DataBuffer sliceFrom(DataBuffer buf, int idx) {
    int len = buf.writePosition() - idx - 1;
    return buf.retainedSlice(idx + 1, len);
  }

  public static void closeChannel(Channel channel) {
    try {
      if (channel.isOpen()) {
        channel.close();
      }
    }
    catch (IOException ignore) {
    }
  }

}
