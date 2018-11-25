package me.volart.rest;

import lombok.extern.slf4j.Slf4j;
import me.volart.dto.Client;
import me.volart.dto.TransferInfo;
import me.volart.exception.AccountException;
import me.volart.exception.ApiParameterException;
import me.volart.exception.ClientNotFound;
import me.volart.exception.MapperException;
import me.volart.service.ClientService;
import me.volart.util.Mapper;

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
    post("/client", (req, res) -> {
      Client client = Mapper.fromJson(req.body(), Client.class);
      service.createClient(client);
      res.status(CREATED_201);
      return "Successfully created";
    });

    get("/client/:clientId", (req, res) -> {
      long id = parsClientId(req.params(PARAM_CLIENT_ID));
      Client client = service.getClient(id);
      return Mapper.toJson(client);
    });

    delete("/client/:clientId", (req, res) -> {
      long id = parsClientId(req.params(PARAM_CLIENT_ID));
      service.deleteClient(id);
      return "Successfully deleted";
    });

    post("/client/:clientId/transfer", (req, res) -> {
      Long id = parsClientId(req.params(PARAM_CLIENT_ID));
      TransferInfo transferInfo = Mapper.fromJson(req.body(), TransferInfo.class);
      service.transferMoney(id, transferInfo);
      return "Money was successfully transferred";
    });

  }

  private void initExceptionMapper() {
    exception(ApiParameterException.class, (exception, req, res) -> {
      res.status(BAD_REQUEST_400);
      res.body(exception.getMessage());
    });

    exception(ClientNotFound.class, (exception, req, res) -> {
      res.status(NOT_FOUND_404);
      res.body(exception.getMessage());
    });

    exception(MapperException.class, (exception, req, res) -> {
      res.status(BAD_REQUEST_400);
      res.body(exception.getMessage());
    });

    exception(AccountException.class, (exception, req, res) -> {
      res.status(BAD_REQUEST_400);
      res.body(exception.getMessage());
    });
  }

  private long parsClientId(String clientIdStr) {
    try {
      return Long.valueOf(clientIdStr);
    } catch (Exception e) {
      throw new ApiParameterException("ClientId should be a number, but -> %s", clientIdStr);
    }
  }
}
