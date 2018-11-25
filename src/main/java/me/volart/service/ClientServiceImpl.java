package me.volart.service;

import me.volart.dao.ClientDao;
import me.volart.dao.model.AccountDto;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.dto.TransferInfo;
import me.volart.exception.AccountException;
import me.volart.exception.ClientNotFound;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static me.volart.util.DataConverter.convertFrom;

public class ClientServiceImpl implements ClientService {

  private final ClientDao clientDao;

  public ClientServiceImpl(ClientDao clientDao) {
    this.clientDao = clientDao;
  }

  @Override
  public void createClient(Client client) {
    checkDuplicateCurrencies(client);
    checkExistence(client);

    ClientDto clientDto = convertFrom(client);
    clientDao.save(clientDto);
  }

  @Override
  public Client getClient(long clientId) {
    ClientDto clientDto = getClientBy(clientId);
    return convertFrom(clientDto);
  }

  @Override
  public void deleteClient(long clientId) {
    clientDao.delete(clientId);
  }

  @Override
  public void transferMoney(long clientId, TransferInfo transferInfo) {
    ClientDto from = getClientBy(clientId);

    long recipientClientId = transferInfo.getClientId();
    ClientDto to = getClientBy(recipientClientId);

    Long fromId = from.getId();
    Long toId = to.getId();

    if (fromId.equals(toId)) {
      throw new AccountException("Can't transfer money to the same account");
    }

    if (fromId > toId) {
      synchronized (from) {
        synchronized (to) {
          transfer(from, to, transferInfo);
        }
      }
    } else {
      synchronized (to) {
        synchronized (from) {
          transfer(from, to, transferInfo);
        }
      }
    }
  }

  private void transfer(ClientDto from, ClientDto to, TransferInfo transferInfo) {
    String currency = transferInfo.getCurrency();
    AccountDto accountFrom = getAccount(from, currency);

    long amountFrom = accountFrom.getAmount();
    long amountTransfer = transferInfo.getAmount();
    if (amountFrom < amountTransfer) {
      throw new AccountException("Not enough money for transfer");
    }

    Optional<AccountDto> optionalAccountTo = to.getAccounts().stream().filter(acc -> acc.getCurrency().equals(currency)).findFirst();
    if (optionalAccountTo.isPresent()) {
      AccountDto accountTo = optionalAccountTo.get();
      accountTo.setAmount(accountTo.getAmount() + amountTransfer);
    } else {
      AccountDto accountTo = new AccountDto();
      accountTo.setAmount(amountTransfer);
      accountTo.setCurrency(currency);
      to.getAccounts().add(accountTo);
    }
    accountFrom.setAmount(amountFrom - amountTransfer);

    clientDao.update(from);
    clientDao.update(to);
  }

  private ClientDto getClientBy(long clientId) {
    ClientDto client = clientDao.findBy(clientId);
    if (client == null) {
      throw new ClientNotFound("There is not client with id = %s", Long.toString(clientId));
    }
    return client;
  }

  private AccountDto getAccount(ClientDto client, String currency) {
    Optional<AccountDto> accountDto = client.getAccounts().stream().filter(acc -> acc.getCurrency().equals(currency)).findFirst();
    if (!accountDto.isPresent()) {
      throw new AccountException("The account with specified currency does not exist");
    }
    return accountDto.get();
  }

  private void checkDuplicateCurrencies(Client client) {
    List<Account> accounts = client.getAccounts();
    Set<String> currencies = new HashSet<>();
    for (Account account : accounts) {
      if (!currencies.add(account.getCurrency())) {
        throw new AccountException("Client (id = %s) has two similar currency accounts", Long.toString(client.getId()));
      }
    }
  }

  private void checkExistence(Client client) {
    long id = client.getId();
    if (clientDao.findBy(id) != null) {
      throw new AccountException("Client (id = %s) already exists", Long.toString(id));
    }
  }
}
