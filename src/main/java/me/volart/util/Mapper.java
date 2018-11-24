package me.volart.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.volart.exception.MapperException;

import java.io.IOException;

@Slf4j
public final class Mapper {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static <T> String toJson(T obj) {
    try {
      return MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      String message = "Can't parse to JSON";
      log.warn(message, e);
      throw new MapperException(message, e);
    }
  }

  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return MAPPER.readValue(json, type);
    } catch (IOException e) {
      String message = "Can't parse from JSON";
      log.warn(message, e);
      throw new MapperException(message, e);
    }
  }
}
