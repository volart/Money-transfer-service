package me.volart;

import me.volart.dto.Client;
import me.volart.util.DataGenerator;
import me.volart.util.Mapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class MoneyTransferServiceIT {

  private javax.ws.rs.client.Client client;


  @BeforeClass
  public static void beforeAll() {
    App.run();
  }

  @AfterClass
  public static void afterAll() {
    Spark.stop();
  }

  @After
  public void tearDown() {
    Optional.ofNullable(client).ifPresent(javax.ws.rs.client.Client::close);
  }

  @Test
  public void testCreationClient_validClient_successfullyCreated() {
    client = ClientBuilder.newBuilder().build();
    Response response = client.target(URI.create("http://localhost:4567/client"))
        .request()
        .post(Entity.json("{\"id\":1,\"accounts\":[{\"amount\":100,\"currency\":\"RUB\"}]}"));
    assertThat(response.getStatus()).isEqualTo(201);
    assertThat(response.readEntity(String.class)).isEqualTo("Successfully created");
  }

  @Test
  public void testCreationClient_inValidClient_successfullyCreated() {
    client = ClientBuilder.newBuilder().build();
    Response response = client.target(URI.create("http://localhost:4567/client"))
        .request()
        .post(Entity.json("{\"id\":1,\"accounts\":[{\"amount\":100,\"currency\":\"RUB\"}]}"));
    assertThat(response.getStatus()).isEqualTo(201);
    assertThat(response.readEntity(String.class)).isEqualTo("Successfully created");
  }

  @Test
  public void testGetClient_validClient_successfullyGetting() {
    Client expected = DataGenerator.createClient(2, "USD", 1000);

    client = ClientBuilder.newBuilder().build();

    client.target(URI.create("http://localhost:4567/client"))
        .request()
        .post(Entity.json(Mapper.toJson(expected)));

    Response response = client.target(URI.create("http://localhost:4567/client/2"))
        .request()
        .get();
    assertThat(response.getStatus()).isEqualTo(200);

    String respBody = response.readEntity(String.class);

    assertThat(Mapper.fromJson(respBody, Client.class)).isEqualTo(expected);
  }


}
