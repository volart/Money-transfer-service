package me.volart.exception;

public class ApiParameterException extends BaseException {

  public ApiParameterException(String message, String... args) {
    super(message, args);
  }
}
