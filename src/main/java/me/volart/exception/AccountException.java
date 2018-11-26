package me.volart.exception;

import me.volart.common.StatusCode;

public class AccountException extends BaseException {

  public AccountException(StatusCode statusCode, String message, String... args) {
    super(statusCode, message, args);
  }
}
