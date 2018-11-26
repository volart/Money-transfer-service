package me.volart.util;

import com.fasterxml.jackson.core.type.TypeReference;
import me.volart.dto.Client;
import me.volart.dto.ResponseInfo;
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

  @Test
  public void testToJson_forResponseIfo() {
    String expected = "{\"message\":\"text_message\",\"data\":{\"id\":1,\"accounts\":[{\"amount\":100,\"currency\":\"RUB\"}]},\"statusCode\":0}";
    Client client = DataGenerator.createClient(1, "RUB", 100);
    ResponseInfo<Client> responseInfo = DataGenerator.createResponseInfo("text_message", client);

    String actual = Mapper.toJson(responseInfo);
    assertEquals(expected, actual);
  }

  @Test
  public void testFromJson_forResponseIfo() {
    Client client = DataGenerator.createClient(2, "USD", 1000);
    ResponseInfo<Client> expected = DataGenerator.createResponseInfo("text_message", client);

    String json = "{\"message\":\"text_message\",\"data\":{\"id\":2,\"accounts\":[{\"amount\":1000,\"currency\":\"USD\"}]},\"statusCode\":0}";
    ResponseInfo<Client> actual = Mapper.fromJson(json, new TypeReference<ResponseInfo<Client>>() {});
    assertEquals(expected, actual);
  }
}