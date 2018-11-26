package me.volart.dto;

import lombok.Data;
import me.volart.common.StatusCode;

@Data
public class ResponseInfo<T> {

  /**
   * Response message. Might be error message or trivial message.
   */
  private String message;

  /**
   * In most cases it is {@link me.volart.dto.Client}, but might be an another data.
   */
  private T data;

  /**
   * Status code makes a response more specific.
   */
  private int statusCode;

  public static ResponseInfo create(String msg, StatusCode statusCode){
    ResponseInfo info = new ResponseInfo();
    info.setMessage(msg);
    info.setStatusCode(statusCode.getCode());
    return info;
  }
}
