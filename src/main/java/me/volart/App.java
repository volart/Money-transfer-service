package me.volart;

import me.volart.dao.ClientDao;
import me.volart.dao.ClientDaoImpl;
import me.volart.rest.ClientApi;
import me.volart.service.ClientService;
import me.volart.service.ClientServiceImpl;

/**
 */
public class App {
  public static void main(String[] args) {
    run();
  }

  public static void run(){
    ClientDao clientDao = new ClientDaoImpl();
    ClientService clientService = new ClientServiceImpl(clientDao);
    new ClientApi(clientService);
  }
}
