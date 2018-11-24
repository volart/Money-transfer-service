package me.volart.exception;

public class ClientNotFound extends BaseException {

  public ClientNotFound(String message, String... args) {
    super(message, args);
  }
}
