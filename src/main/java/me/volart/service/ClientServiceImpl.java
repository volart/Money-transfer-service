package me.volart.service;

import me.volart.dao.ClientDao;
import me.volart.dao.model.AccountDto;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.dto.TransferInfo;
import me.volart.exception.AccountException;
import me.volart.exception.ClientNotFound;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientServiceImpl implements ClientService {

  private final ClientDao clientDao;

  public ClientServiceImpl(ClientDao clientDao) {
    this.clientDao = clientDao;
  }


  @Override
  public void createClient(Client client) {
    ClientDto clientDto = new ClientDto();
    clientDto.setId(client.getId());
    List<AccountDto> accountDtos = new ArrayList<>();
    for (Account account : client.getAccounts()) {
      AccountDto accountDto = new AccountDto();
      accountDto.setCurrency(account.getCurrency());
      accountDto.setAmount(account.getAmount());
      accountDtos.add(accountDto);
    }
    clientDto.setAccounts(accountDtos);
    clientDao.save(clientDto);
  }

  @Override
  public Client getClient(long clientId) {
    ClientDto clientDto = clientDao.findBy(clientId);

    return null;
  }

  @Override
  public void updateClient(Client client) {

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

    if (clientId > recipientClientId) {
      synchronized (from) {
        synchronized (to) {
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
      }
    } else {
      synchronized (to) {
        synchronized (from) {

        }
      }
    }
  }

  private ClientDto getClientBy(long clientId) {
    ClientDto client = clientDao.findBy(clientId);
    if (client == null) {
      throw new ClientNotFound("There is not client with id = %d", Long.toString(clientId));
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
}
