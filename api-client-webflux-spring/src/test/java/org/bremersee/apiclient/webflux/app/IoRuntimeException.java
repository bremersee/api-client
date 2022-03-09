package org.bremersee.apiclient.webflux.app;

import java.io.IOException;

public class IoRuntimeException extends RuntimeException {

  public IoRuntimeException(String message, IOException cause) {
    super(message, cause);
  }
}
