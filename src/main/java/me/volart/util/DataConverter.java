package me.volart.util;

import me.volart.dao.model.AccountDto;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;

import java.util.ArrayList;
import java.util.List;

public final class DataConverter {

  public static ClientDto convertFrom(Client client){
    ClientDto clientDto = new ClientDto();
    List<AccountDto> accountDtos = new ArrayList<>();
    for (Account account : client.getAccounts()) {
      AccountDto accountDto = new AccountDto();
      accountDto.setCurrency(account.getCurrency());
      accountDto.setAmount(account.getAmount());
      accountDtos.add(accountDto);
    }
    clientDto.setId(client.getId());
    clientDto.setAccounts(accountDtos);
    return clientDto;
  }

  public static Client convertFrom(ClientDto clientDto){
    Client client = new Client();
    List<Account> accounts = new ArrayList<>();
    for (AccountDto accountDto : clientDto.getAccounts()) {
      Account account = new Account();
      account.setAmount(accountDto.getAmount());
      account.setCurrency(accountDto.getCurrency());
      accounts.add(account);
    }
    client.setId(clientDto.getId());
    client.setAccounts(accounts);
    return client;
  }
}
