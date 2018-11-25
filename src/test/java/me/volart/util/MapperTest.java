package me.volart.util;

import me.volart.dto.Client;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapperTest {

  @Test
  public void testToJson_forClient() {
    String expected = "{\"id\":1,\"accounts\":[{\"amount\":100,\"currency\":\"RUB\"}]}";
    Client client = DataGenerator.createClient(1, "RUB", 100);

    String actual = Mapper.toJson(client);
    assertEquals(expected, actual);
  }

  @Test
  public void testFromJson_forClient() {
    Client expected = DataGenerator.createClient(2, "USD", 1000);

    String json = "{\"id\":2,\"accounts\":[{\"amount\":1000,\"currency\":\"USD\"}]}";
    Client actual = Mapper.fromJson(json, Client.class);
    assertEquals(expected, actual);
  }
}