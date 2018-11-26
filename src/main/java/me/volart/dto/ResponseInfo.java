package me.volart.dto;

import lombok.Data;

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

  public static ResponseInfo create(String msg){
    ResponseInfo info = new ResponseInfo();
    info.setMessage(msg);
    return info;
  }
}
