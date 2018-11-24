package me.volart.dao;

import me.volart.dao.model.ClientDto;

public interface ClientDao {

  void save(ClientDto client);

  ClientDto findBy(long id);

  void update(ClientDto client);

  void delete(long id);

}
