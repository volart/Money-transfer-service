package me.volart;

import com.fasterxml.jackson.core.type.TypeReference;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.dto.ResponseInfo;
import me.volart.dto.TransferInfo;
import me.volart.util.DataGenerator;
import me.volart.util.Mapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static me.volart.common.StatusCode.ACCOUNT_DOES_NOT_EXIST;
import static me.volart.common.StatusCode.CLIENT_ALREADY_EXISTS;
import static me.volart.common.StatusCode.CLIENT_DOES_NOT_EXIST;
import static me.volart.common.StatusCode.INVALID_ID;
import static me.volart.common.StatusCode.NOT_ENOUGH_MONEY;
import static me.volart.common.StatusCode.OK;
import static me.volart.common.StatusCode.PARSER_ERROR;
import static me.volart.common.StatusCode.SAME_ACCOUNT;
import static me.volart.common.StatusCode.SAME_CLIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


public class MoneyTransferServiceIT {

  private javax.ws.rs.client.Client restApi;


  @BeforeClass
  public static void beforeAll() {
    App.run();
  }

  @AfterClass
  public static void afterAll() {
    Spark.stop();
  }

  @Before
  public void setup() {
    restApi = ClientBuilder.newBuilder().build();
  }

  @After
  public void tearDown() {
    restApi.close();
  }

  @Test
  public void testCreationClient_validClient_successfullyCreated() {
    Client client = DataGenerator.createClient(1, "RUB", 100);
    Response response = createClient(client);
    assertThat(response.getStatus()).isEqualTo(201);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Successfully created", OK));
    delete(1);
  }

  @Test
  public void testCreationClient_inValidClient_badRequest() {
    Response response = restApi.target(URI.create("http://localhost:4567/client"))
        .request()
        .post(Entity.json("{\"id\":1,\"account\":[{\"amount\":100,\"currency\":\"RUB\"}]}"));
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Can't parse from JSON", PARSER_ERROR));
  }

  @Test
  public void testCreationClient_twoSimilarCurrencyClient_badRequest() {
    Response response = restApi.target(URI.create("http://localhost:4567/client"))
        .request()
        .post(Entity.json("{\"id\":2,\"accounts\":[{\"amount\":1000,\"currency\":\"USD\"},{\"amount\":1000,\"currency\":\"USD\"}]}"));
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Client (id = 2) has two similar currency accounts", SAME_ACCOUNT));
  }

  @Test
  public void testCreationClient_clientAlreadyExists_badRequest() {
    Client client = DataGenerator.createClient(1, "RUB", 100);
    createClient(client);
    Response response = createClient(client);
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Client (id = 1) already exists", CLIENT_ALREADY_EXISTS));
    delete(1);
  }

  @Test
  public void testGetClient_validClient_successfullyGetting() {
    Client expected = DataGenerator.createClient(2, "USD", 1000);
    createClient(expected);
    Response response = restApi.target(URI.create("http://localhost:4567/client/2"))
        .request()
        .get();
    assertThat(response.getStatus()).isEqualTo(200);
    ResponseInfo<Client> responseInfo = parseResponseWithData(response);
    assertThat(responseInfo.getData()).isEqualTo(expected);
    delete(2);
  }

  @Test
  public void testGetClient_invalidId_successfullyGetting() {
    Response response = restApi.target(URI.create("http://localhost:4567/client/invalidId"))
        .request()
        .get();
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("ClientId should be a number, but -> invalidId", INVALID_ID));
  }

  @Test
  public void testGetClient_nonexistentClient_notFound() {
    Response response = restApi.target(URI.create("http://localhost:4567/client/1000000000"))
        .request()
        .get();
    assertThat(response.getStatus()).isEqualTo(404);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("There is not client with id = 1000000000", CLIENT_DOES_NOT_EXIST));
  }

  @Test
  public void testDeleteClient_existingClient_successfullyDeleted() {
    Client expected = DataGenerator.createClient(3, "USD", 1000);
    createClient(expected);
    Response response = delete(3);
    assertThat(response.getStatus()).isEqualTo(200);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Successfully deleted", OK));
  }

  @Test
  public void testMoneyTransfer_TwoClientsHaveSameCurrencyAccounts_successfullyTransferred() {
    Client from = DataGenerator.createClient(1, "USD", 1000);
    Client to = DataGenerator.createClient(2, "USD", 1000);
    createClient(from);
    createClient(to);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(to.getId());
    info.setCurrency("USD");
    Response response = transfer(from.getId(), info);
    assertThat(response.getStatus()).isEqualTo(200);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Money was successfully transferred", OK));

    from = getClient(1);
    Account accountFrom = from.getAccounts().get(0);
    assertThat(accountFrom.getAmount()).isEqualTo(0);
    to = getClient(2);
    Account accountTo = to.getAccounts().get(0);
    assertThat(accountTo.getAmount()).isEqualTo(2000);

    delete(1);
    delete(2);
  }

  @Test
  public void testMoneyTransfer_TwoClientsHaveDifferentCurrencyAccounts_successfullyTransferred() {
    Client from = DataGenerator.createClient(1, "USD", 1000);
    Client to = DataGenerator.createClient(2, "RUB", 1000);
    createClient(from);
    createClient(to);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(to.getId());
    info.setCurrency("USD");
    Response response = transfer(from.getId(), info);
    assertThat(response.getStatus()).isEqualTo(200);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Money was successfully transferred", OK));

    from = getClient(1);
    Account accountFrom = from.getAccounts().get(0);
    assertThat(accountFrom.getAmount()).isEqualTo(0);
    to = getClient(2);
    Optional<Account> optionalAccount = to.getAccounts().stream().filter(a -> a.getCurrency().equals("USD")).findFirst();
    assertThat(optionalAccount.isPresent()).isEqualTo(true);
    Account accountTo = optionalAccount.get();
    assertThat(accountTo.getAmount()).isEqualTo(1000);

    delete(1);
    delete(2);
  }

  @Test
  public void testMoneyTransfer_TwoClientsWithoutEnoughMoney_deniedTransfer() {
    Client from = DataGenerator.createClient(1, "USD", 0);
    Client to = DataGenerator.createClient(2, "USD", 1000);
    createClient(from);
    createClient(to);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(to.getId());
    info.setCurrency("USD");
    Response response = transfer(from.getId(), info);
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Not enough money for transfer", NOT_ENOUGH_MONEY));

    delete(1);
    delete(2);
  }

  @Test
  public void testMoneyTransfer_recipientClientsDoesNotExist_deniedTransfer() {
    Client from = DataGenerator.createClient(1, "USD", 1000);
    createClient(from);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(2);
    info.setCurrency("USD");
    Response response = transfer(from.getId(), info);
    assertThat(response.getStatus()).isEqualTo(404);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("There is not client with id = 2", CLIENT_DOES_NOT_EXIST));

    delete(1);
  }

  @Test
  public void testMoneyTransfer_senderClientsDoesNotExist_deniedTransfer() {
    Client to = DataGenerator.createClient(2, "USD", 1000);
    createClient(to);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(to.getId());
    info.setCurrency("USD");
    Response response = transfer(1, info);
    assertThat(response.getStatus()).isEqualTo(404);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("There is not client with id = 1", CLIENT_DOES_NOT_EXIST));

    delete(2);
  }

  @Test
  public void testMoneyTransfer_senderClientsDoesNotHaveAccount_deniedTransfer() {
    Client from = DataGenerator.createClient(1, "RUB", 1000);
    Client to = DataGenerator.createClient(2, "USD", 1000);
    createClient(from);
    createClient(to);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(to.getId());
    info.setCurrency("USD");
    Response response = transfer(from.getId(), info);
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("The account with specified currency does not exist", ACCOUNT_DOES_NOT_EXIST));

    delete(1);
    delete(2);
  }

  @Test
  public void testMoneyTransfer_sendToItself_deniedTransfer() {
    Client fromTo = DataGenerator.createClient(1, "RUB", 1000);

    createClient(fromTo);

    TransferInfo info = new TransferInfo();
    info.setAmount(1000);
    info.setClientId(fromTo.getId());
    info.setCurrency("USD");
    Response response = transfer(fromTo.getId(), info);
    assertThat(response.getStatus()).isEqualTo(400);
    ResponseInfo responseInfo = parseResponse(response);
    assertThat(responseInfo).isEqualTo(ResponseInfo.create("Can't transfer money to the same client", SAME_CLIENT));

    delete(1);
  }


  @Test
  public void testMoneyTransfer_nClients_successfullyTransferred() {
    int n = 10;
    //Should be greater than n
    int amount = 99;

    //Creating 100 clients with 1 USD each
    for (int i = 1; i <= n; i++) {
      Client to = DataGenerator.createClient(i, "USD", amount);
      createClient(to);
    }

    Executor executor = Executors.newFixedThreadPool(5);
    AtomicInteger counter = new AtomicInteger(1);
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= n; j++) {
        if (i != j) {
          executor.execute(runTransfer(i, j, counter));
        }
      }
    }
    await().atMost(25, SECONDS).until(() -> counter.get() == (n * n) - (n - 1));

    //Checking any random account has a start amount
    int id = new Random().nextInt(n) + 1;
    Client client = getClient(id);
    Account account = client.getAccounts().get(0);
    assertThat(account.getAmount()).isEqualTo(amount);


    for (int i = 1; i <= n; i++) {
      delete(i);
    }
  }

  private Runnable runTransfer(int from, int to, AtomicInteger counter) {
    return () -> {
      javax.ws.rs.client.Client restApi = ClientBuilder.newBuilder().build();
      TransferInfo info = new TransferInfo();
      info.setAmount(1);
      info.setClientId(to);
      info.setCurrency("USD");
      restApi.target(URI.create("http://localhost:4567/client/" + from + "/transfer"))
          .request()
          .post(Entity.json(Mapper.toJson(info)));
      restApi.close();

      counter.incrementAndGet();
    };
  }

  private Response createClient(Client c) {
    return restApi.target(URI.create("http://localhost:4567/client"))
        .request()
        .post(Entity.json(Mapper.toJson(c)));
  }

  private Response delete(long id) {
    return restApi.target(URI.create("http://localhost:4567/client/" + id))
        .request()
        .delete();
  }

  private Client getClient(long id) {
    Response response = restApi.target(URI.create("http://localhost:4567/client/" + id))
        .request()
        .get();

    ResponseInfo<Client> responseInfo = parseResponseWithData(response);
    return responseInfo.getData();
  }

  private Response transfer(long id, TransferInfo transferInfo) {
    return restApi.target(URI.create("http://localhost:4567/client/" + id + "/transfer"))
        .request()
        .post(Entity.json(Mapper.toJson(transferInfo)));
  }

  private  ResponseInfo parseResponse(Response response){
    String json = response.readEntity(String.class);
    return Mapper.fromJson(json, new TypeReference<ResponseInfo>() {});
  }

  private ResponseInfo<Client> parseResponseWithData(Response response){
    String json = response.readEntity(String.class);
    return Mapper.fromJson(json, new TypeReference<ResponseInfo<Client>>() {});
  }
}
