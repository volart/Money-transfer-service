package me.volart.common;

public enum StatusCode {

  OK(0),
  SAME_CLIENT(1),
  SAME_ACCOUNT(2),
  NOT_ENOUGH_MONEY(3),
  CLIENT_DOES_NOT_EXIST(4),
  ACCOUNT_DOES_NOT_EXIST(5),
  CLIENT_ALREADY_EXISTS(6),
  INVALID_ID(7),
  PARSER_ERROR(8),
  INVALID_CURRENCY(9);

  private final int code;

  StatusCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
