package me.volart.rest;

import lombok.extern.slf4j.Slf4j;
import me.volart.dto.TransferInfo;
import me.volart.exception.ApiParameterException;
import me.volart.exception.ClientNotFound;
import me.volart.service.ClientService;
import me.volart.util.Mapper;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

@Slf4j
public class ClientApi {

  private final ClientService service;

  public ClientApi(ClientService service) {
    this.service = service;

    initCrudApi();
    initTransferMoneyApi();
    initExceptionMapper();
  }

  private void initCrudApi() {
    post("/client/:clientId", (req, res) -> {
      return null;
    });

    get("/client/:clientId", (req, res) -> {

      return null;
    });

    put("/client/:clientId", (req, res) -> {
      log.info("put: " + req.body());
      return null;
    });

    delete("/client/:clientId", (req, res) -> {
      log.info("delete: " + req.body());
      return null;
    });
  }

  private void initTransferMoneyApi() {
    post("/client/:clientId/transfer", (req, res) -> {
      Long id = parsClientId(req.params(":clientId"));
      TransferInfo transferInfo = Mapper.fromJson(req.body(), TransferInfo.class);
      service.transferMoney(id, transferInfo);
      return null;
    });
  }

  private void initExceptionMapper() {
    exception(ApiParameterException.class, (exception, req, res) -> {
      res.status(400);
      res.body(exception.getMessage());
    });

    exception(ClientNotFound.class, (exception, req, res) -> {
      res.status(404);
      res.body(exception.getMessage());
    });
  }

  private Long parsClientId(String clientIdStr ){
    try {
       return Long.valueOf(clientIdStr);
    } catch (Exception e) {
      throw new ApiParameterException("ClientId should be a number, but -> %s", clientIdStr);
    }
  }
}
