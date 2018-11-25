package me.volart.util;

import me.volart.dao.model.AccountDto;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;

import java.util.ArrayList;
import java.util.List;

public final class DataGenerator {

  public static Client createClient(long id, String currency, long amount){
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

  public static ClientDto createClientDto(long id, String currency, long amount){
    ClientDto clientDto = new ClientDto();
    clientDto.setId(id);
    List<AccountDto> accountDtos = new ArrayList<>();
    AccountDto accountDto = new AccountDto();
    accountDto.setAmount(amount);
    accountDto.setCurrency(currency);
    accountDtos.add(accountDto);
    clientDto.setAccounts(accountDtos);
    return clientDto;
  }
}
