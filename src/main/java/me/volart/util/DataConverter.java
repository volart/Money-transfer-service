package me.volart.util;

import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataConverter {

  private DataConverter() {}

  public static ClientDto convertFrom(Client client){
    ClientDto clientDto = new ClientDto();
    Map<String, Long> accounts = new HashMap<>();
    for (Account account : client.getAccounts()) {
      accounts.put(account.getCurrency(), account.getAmount());
    }
    clientDto.setId(client.getId());
    clientDto.setAccounts(accounts);
    return clientDto;
  }

  public static Client convertFrom(ClientDto clientDto){
    Client client = new Client();
    List<Account> accounts = new ArrayList<>();
    for (Map.Entry<String, Long> accountDto : clientDto.getAccounts().entrySet()) {
      Account account = new Account();
      account.setCurrency(accountDto.getKey());
      account.setAmount(accountDto.getValue());
      accounts.add(account);
    }
    client.setId(clientDto.getId());
    client.setAccounts(accounts);
    return client;
  }
}
