package me.volart.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.volart.exception.MapperException;

import java.io.IOException;

public final class Mapper {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private Mapper() {}

  public static <T> String toJson(T obj) {
    try {
      return OBJECT_MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      String message = "Can't parse to JSON";
      throw new MapperException(message, e);
    }
  }

  public static <T> T fromJson(String json, Class<T> type) {
    try {
      return OBJECT_MAPPER.readValue(json, type);
    } catch (IOException e) {
      String message = "Can't parse from JSON";
      throw new MapperException(message, e);
    }
  }
}
