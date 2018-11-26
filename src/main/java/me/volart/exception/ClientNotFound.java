package me.volart.exception;

import me.volart.common.StatusCode;

public class ClientNotFound extends BaseException {

  public ClientNotFound(StatusCode statusCode, String message, String... args) {
    super(statusCode, message, args);
  }
}
