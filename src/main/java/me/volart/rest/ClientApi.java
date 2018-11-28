package me.volart.rest;

import lombok.extern.slf4j.Slf4j;
import me.volart.dto.Client;
import me.volart.dto.ResponseInfo;
import me.volart.dto.TransferInfo;
import me.volart.exception.AccountException;
import me.volart.exception.ApiParameterException;
import me.volart.exception.BaseException;
import me.volart.exception.ClientNotFound;
import me.volart.exception.MapperException;
import me.volart.service.ClientService;
import me.volart.util.Mapper;
import spark.Request;
import spark.Response;

import static me.volart.common.StatusCode.INVALID_ID;
import static me.volart.common.StatusCode.OK;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

@Slf4j
public class ClientApi {

  private static final String PARAM_CLIENT_ID = ":clientId";

  private final ClientService service;

  public ClientApi(ClientService service) {
    this.service = service;

    initApi();
    initExceptionMapper();
  }

  private void initApi() {
    post("/client", this::createClient);
    get("/client/:clientId", this::getClient);
    delete("/client/:clientId", this::deleteClient);
    post("/client/:clientId/transfer", this::transferMoney);
  }

  private void initExceptionMapper() {
    exception(ApiParameterException.class, (exception, req, res) -> handleException(exception, res, BAD_REQUEST_400));
    exception(ClientNotFound.class, (exception, req, res) -> handleException(exception, res, NOT_FOUND_404));
    exception(MapperException.class, (exception, req, res) -> handleException(exception, res, BAD_REQUEST_400));
    exception(AccountException.class, (exception, req, res) -> handleException(exception, res, BAD_REQUEST_400));
  }

  private String createClient(Request req, Response res) {
    Client client = Mapper.fromJson(req.body(), Client.class);
    service.createClient(client);
    res.status(CREATED_201);
    return Mapper.toJson(ResponseInfo.create("Successfully created", OK));
  }

  private String getClient(Request req, Response res) {
    long id = parsClientId(req.params(PARAM_CLIENT_ID));
    Client client = service.getClient(id);
    ResponseInfo<Client> responseInfo = new ResponseInfo<>();
    responseInfo.setData(client);
    return Mapper.toJson(responseInfo);
  }

  private String deleteClient(Request req, Response res) {
    long id = parsClientId(req.params(PARAM_CLIENT_ID));
    service.deleteClient(id);
    return Mapper.toJson(ResponseInfo.create("Successfully deleted", OK));
  }

  private String transferMoney(Request req, Response res) {
    Long id = parsClientId(req.params(PARAM_CLIENT_ID));
    TransferInfo transferInfo = Mapper.fromJson(req.body(), TransferInfo.class);
    service.transferMoney(id, transferInfo);
    return Mapper.toJson(ResponseInfo.create("Money was successfully transferred", OK));
  }

  private <T extends BaseException> void handleException(T exception, Response res, int statusCode) {
    res.status(statusCode);
    res.body(Mapper.toJson(ResponseInfo.create(exception.getMessage(), exception.getStatusCode())));
  }

  private long parsClientId(String clientIdStr) {
    try {
      return Long.valueOf(clientIdStr);
    } catch (Exception e) {
      throw new ApiParameterException(INVALID_ID, "ClientId should be a number, but -> %s", clientIdStr);
    }
  }
}
