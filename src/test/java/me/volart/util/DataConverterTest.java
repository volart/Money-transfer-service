package me.volart.util;

import me.volart.dao.model.ClientDto;
import me.volart.dto.Client;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataConverterTest {

  @Test
  public void testConvertFromClient_toClientDto() {
    Client client = DataGenerator.createClient(3, "UAH", 2000);
    ClientDto expected = DataGenerator.createClientDto(3, "UAH", 2000);

    ClientDto actual = DataConverter.convertFrom(client);
    assertEquals(expected, actual);
  }

  @Test
  public void testConvertFromClientDto_toClient() {
    ClientDto clientDto = DataGenerator.createClientDto(4, "GPB", 3000);
    Client expected = DataGenerator.createClient(4, "GPB", 3000);

    Client actual = DataConverter.convertFrom(clientDto);
    assertEquals(expected, actual);
  }
}