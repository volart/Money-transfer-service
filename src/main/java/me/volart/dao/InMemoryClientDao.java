package me.volart.dao;

import me.volart.dao.model.ClientDto;
import me.volart.exception.AccountException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.volart.common.StatusCode.CLIENT_ALREADY_EXISTS;


public class InMemoryClientDao implements ClientDao {

  private final Map<Long, ClientDto> storage = new ConcurrentHashMap<>();

  @Override
  public void save(ClientDto client) {
    ClientDto existed = storage.putIfAbsent(client.getId(), client);
    if(existed != null) {
      throw new AccountException(CLIENT_ALREADY_EXISTS, "Client (id = %s) already exists", existed.getId().toString());
    }
  }

  @Override
  public ClientDto findBy(long id) {
    return storage.get(id);
  }

  @Override
  public void update(ClientDto client) {
    storage.put(client.getId(), client);
  }

  @Override
  public void delete(long id) {
    storage.remove(id);
  }
}
