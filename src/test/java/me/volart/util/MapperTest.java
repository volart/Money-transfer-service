package me.volart.util;

import com.fasterxml.jackson.core.type.TypeReference;
import me.volart.dto.Client;
import me.volart.dto.ResponseInfo;
import me.volart.dto.TransferInfo;
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
  public void testToJson_forResponseInfo() {
    String expected = "{\"message\":\"text_message\",\"data\":{\"id\":1,\"accounts\":[{\"amount\":100,\"currency\":\"RUB\"}]},\"statusCode\":0}";
    Client client = DataGenerator.createClient(1, "RUB", 100);
    ResponseInfo<Client> responseInfo = DataGenerator.createResponseInfo("text_message", client);

    String actual = Mapper.toJson(responseInfo);
    assertEquals(expected, actual);
  }

  @Test
  public void testFromJson_forResponseInfo() {
    Client client = DataGenerator.createClient(2, "USD", 1000);
    ResponseInfo<Client> expected = DataGenerator.createResponseInfo("text_message", client);

    String json = "{\"message\":\"text_message\",\"data\":{\"id\":2,\"accounts\":[{\"amount\":1000,\"currency\":\"USD\"}]},\"statusCode\":0}";
    ResponseInfo<Client> actual = Mapper.fromJson(json, new TypeReference<ResponseInfo<Client>>() {});
    assertEquals(expected, actual);
  }

  @Test
  public void testToJson_forTransferInfo() {
    String expected = "{\"clientId\":1,\"amount\":100,\"currency\":\"USD\"}";
    TransferInfo transferInfo = new TransferInfo();
    transferInfo.setAmount(100);
    transferInfo.setClientId(1);
    transferInfo.setCurrency("USD");

    String actual = Mapper.toJson(transferInfo);
    assertEquals(expected, actual);
  }

  @Test
  public void testFromJson_forTransferInfo() {
    TransferInfo expected = new TransferInfo();
    expected.setAmount(100);
    expected.setClientId(1);
    expected.setCurrency("USD");
    String json = "{\"clientId\":1,\"amount\":100,\"currency\":\"USD\"}";
    TransferInfo actual = Mapper.fromJson(json, TransferInfo.class);
    assertEquals(expected, actual);
  }
}