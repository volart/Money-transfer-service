package me.volart.service;

import me.volart.dto.Client;
import me.volart.dto.TransferInfo;

public interface ClientService {

  void createClient(Client client);

  Client getClient(long clientId);

  void updateClient(Client client);

  void deleteClient(long clientId);

  void transferMoney(long clientId, TransferInfo transferInfo);
}
