package me.volart.exception;

import me.volart.common.StatusCode;

public class ApiParameterException extends BaseException {

  public ApiParameterException(StatusCode statusCode, String message, String... args) {
    super(statusCode, message, args);
  }
}
