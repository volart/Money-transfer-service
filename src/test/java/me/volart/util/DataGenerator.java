package me.volart.util;

import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.dto.ResponseInfo;
import me.volart.dto.TransferInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataGenerator {

  public static Client createClient(long id, String currency, long amount) {
    Client client = new Client();
    client.setId(id);
    List<Account> accounts = new ArrayList<>();
    Account account = new Account();
    account.setAmount(amount);
    account.setCurrency(currency);
    accounts.add(account);
    client.setAccounts(accounts);
    return client;
  }

  public static ClientDto createClientDto(long id, String currency, long amount) {
    ClientDto clientDto = new ClientDto();
    clientDto.setId(id);
    Map<String, Long> accountDtos = new HashMap<>();
    accountDtos.put(currency, amount);
    clientDto.setAccounts(accountDtos);
    return clientDto;
  }

  public static <T> ResponseInfo<T> createResponseInfo(String msg, T obj) {
    ResponseInfo<T> responseInfo = new ResponseInfo<>();
    responseInfo.setData(obj);
    responseInfo.setMessage(msg);
    return responseInfo;
  }

  public static TransferInfo createTransferInfo(long id, String currency, long amount){
    TransferInfo info = new TransferInfo();
    info.setClientId(id);
    info.setCurrency(currency);
    info.setAmount(amount);
    return info;
  }
}
