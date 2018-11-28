package me.volart.service;

import lombok.extern.slf4j.Slf4j;
import me.volart.dao.ClientDao;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.dto.TransferInfo;
import me.volart.exception.AccountException;
import me.volart.exception.ClientNotFound;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.volart.common.StatusCode.ACCOUNT_DOES_NOT_EXIST;
import static me.volart.common.StatusCode.CLIENT_ALREADY_EXISTS;
import static me.volart.common.StatusCode.CLIENT_DOES_NOT_EXIST;
import static me.volart.common.StatusCode.INVALID_CURRENCY;
import static me.volart.common.StatusCode.NOT_ENOUGH_MONEY;
import static me.volart.common.StatusCode.SAME_ACCOUNT;
import static me.volart.common.StatusCode.SAME_CLIENT;
import static me.volart.util.DataConverter.convertFrom;

@Slf4j
public class ClientServiceImpl implements ClientService {

  private final ClientDao clientDao;

  public ClientServiceImpl(ClientDao clientDao) {
    this.clientDao = clientDao;
  }

  @Override
  public void createClient(Client client) {
    log.info("Started creation client with id = {}", client.getId());
    checkDuplicateCurrencies(client);
    checkExistence(client);
    for (Account account : client.getAccounts()) {
      checkCurrency(account.getCurrency());
    }

    ClientDto clientDto = convertFrom(client);
    log.info(clientDto.toString());
    clientDao.save(clientDto);
    log.info("Created client with id = {}", client.getId());
  }

  @Override
  public Client getClient(long clientId) {
    ClientDto clientDto = getClientBy(clientId);
    return convertFrom(clientDto);
  }

  @Override
  public void deleteClient(long clientId) {
    clientDao.delete(clientId);
    log.info("Deleted client with id = {}", clientId);
  }

  @Override
  public void transferMoney(long clientId, TransferInfo transferInfo) {
    checkCurrency(transferInfo.getCurrency());

    ClientDto from = getClientBy(clientId);

    Long toId = transferInfo.getClientId();
    ClientDto to = getClientBy(toId);

    Long fromId = from.getId();

    if (fromId.equals(toId)) {
      throw new AccountException(SAME_CLIENT, "Can't transfer money to the same client");
    }

    if (fromId > toId) {
      synchronized (from.getSyncObj()) {
        synchronized (to.getSyncObj()) {
          transfer(from, to, transferInfo);
        }
      }
    } else {
      synchronized (to.getSyncObj()) {
        synchronized (from.getSyncObj()) {
          transfer(from, to, transferInfo);
        }
      }
    }
    log.info("Money was transferred from {} to {}", fromId, toId);
  }

  protected void transfer(ClientDto from, ClientDto to, TransferInfo transferInfo) {
    String currency = transferInfo.getCurrency();

    long amountFrom = getAmount(from, currency);
    long amountTransfer = transferInfo.getAmount();
    if (amountFrom < amountTransfer) {
      throw new AccountException(NOT_ENOUGH_MONEY, "Not enough money for transfer");
    }

    Long amountTo = to.getAccounts().get(currency);
    if (amountTo != null) {
      amountTo += amountTransfer;
      to.getAccounts().put(currency, amountTo);
    } else {
      to.getAccounts().put(currency, amountTransfer);
    }
    from.getAccounts().put(currency, amountFrom - amountTransfer);

    clientDao.update(from);
    clientDao.update(to);
  }

  protected ClientDto getClientBy(long clientId) {
    ClientDto client = clientDao.findBy(clientId);
    if (client == null) {
      throw new ClientNotFound(CLIENT_DOES_NOT_EXIST, "There is not client with id = %s", Long.toString(clientId));
    }
    return client;
  }

  protected Long getAmount(ClientDto client, String currency) {
    Long amount = client.getAccounts().get(currency);
    if (amount == null) {
      throw new AccountException(ACCOUNT_DOES_NOT_EXIST, "The account with specified currency does not exist");
    }
    return amount;
  }

  protected void checkExistence(Client client) {
    long id = client.getId();
    if (clientDao.findBy(id) != null) {
      throw new AccountException(CLIENT_ALREADY_EXISTS, "Client (id = %s) already exists", Long.toString(id));
    }
  }

  protected void checkDuplicateCurrencies(Client client) {
    List<Account> accounts = client.getAccounts();
    Set<String> currencies = new HashSet<>();
    for (Account account : accounts) {
      if (!currencies.add(account.getCurrency())) {
        throw new AccountException(SAME_ACCOUNT, "Client (id = %s) has two similar currency accounts", Long.toString(client.getId()));
      }
    }
  }

  protected void checkCurrency(String currency) {
    try {
      Currency.getInstance(currency);
    } catch (IllegalArgumentException iae) {
      throw new AccountException(INVALID_CURRENCY, "The currency code %s is not allowed", currency);
    }
  }
}
