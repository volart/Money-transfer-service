package me.volart.exception;

import me.volart.common.StatusCode;

public class MapperException extends BaseException {

  public MapperException(StatusCode statusCode, String message, Throwable cause) {
    super(statusCode, message, cause);
  }
}
