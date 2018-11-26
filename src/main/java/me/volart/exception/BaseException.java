package me.volart.exception;

import me.volart.common.StatusCode;

public class BaseException extends RuntimeException {

  private final StatusCode statusCode;

  public BaseException(StatusCode statusCode, String message, String... args) {
    super(String.format(message, args));
    this.statusCode = statusCode;
  }

  public BaseException(StatusCode statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }
}
