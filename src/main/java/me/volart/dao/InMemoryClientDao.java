package me.volart.dao;

import me.volart.dao.model.ClientDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryClientDao implements ClientDao {

  private final Map<Long, ClientDto> storage = new ConcurrentHashMap<>();

  @Override
  public void save(ClientDto client) {
    storage.putIfAbsent(client.getId(), client);
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
