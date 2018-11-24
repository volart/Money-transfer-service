package me.volart.exception;

public class BaseException extends RuntimeException {

  public BaseException(String message, String... args) {
    super(String.format(message, args));
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
