package me.volart.service;

import me.volart.dao.InMemoryClientDao;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.exception.AccountException;
import me.volart.exception.ClientNotFound;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientServiceImplTest {

  private ClientServiceImpl clientService;

  private InMemoryClientDao clientDao = mock(InMemoryClientDao.class);

  @Before
  public void setUp() {
    clientService = new ClientServiceImpl(clientDao);
  }

  @Test
  public void testGetClientBy_clientExists_fine() {
    long id = 1;
    ClientDto expected = new ClientDto();
    when(clientDao.findBy(id)).thenReturn(expected);
    ClientDto actual = clientService.getClientBy(id);
    assertEquals(expected, actual);
  }

  @Test(expected = ClientNotFound.class)
  public void testGetClientBy_clientDoesNotExist_exception() {
    long id = 1;
    when(clientDao.findBy(id)).thenReturn(null);
    clientService.getClientBy(id);
  }

  @Test
  public void testGetAccount_accountExists_fine() {
    String usd = "USD";
    ClientDto client = new ClientDto();
    Map<String, Long> accounts = new HashMap<>();
    long expected = 100L;
    accounts.put(usd, expected);
    client.setAccounts(accounts);
    long actual = clientService.getAmount(client, usd);
    assertEquals(expected, actual);
  }

  @Test(expected = AccountException.class)
  public void testGetAccount_accountDoesNotExist_exception() {
    String usd = "USD";
    ClientDto client = new ClientDto();

    client.setAccounts(new HashMap<>());
    clientService.getAmount(client, usd);
  }

  @Test
  public void testCheckExistence_doesNotExist_fine() {
    long id = 1;
    when(clientDao.findBy(id)).thenReturn(null);
    Client client = new Client();
    client.setId(id);
    clientService.checkExistence(client);
  }

  @Test(expected = AccountException.class)
  public void testCheckExistence_exists_exception() {
    long id = 1;
    when(clientDao.findBy(id)).thenReturn(new ClientDto());
    Client client = new Client();
    client.setId(id);
    clientService.checkExistence(client);
  }

  @Test
  public void testCheckDuplicateCurrencies_noDuplicates_fine() {
    Client client = new Client();
    List<Account> accounts = new ArrayList<>();

    Account usd = new Account();
    usd.setCurrency("USD");
    usd.setAmount(100);
    accounts.add(usd);

    Account rub = new Account();
    rub.setCurrency("RUB");
    rub.setAmount(100);
    accounts.add(rub);

    client.setAccounts(accounts);
    clientService.checkDuplicateCurrencies(client);
  }

  @Test(expected = AccountException.class)
  public void testCheckDuplicateCurrencies_havDuplicates_exception() {
    Client client = new Client();
    List<Account> accounts = new ArrayList<>();

    Account usd1 = new Account();
    usd1.setCurrency("USD");
    usd1.setAmount(100);
    accounts.add(usd1);

    Account usd2 = new Account();
    usd2.setCurrency("USD");
    usd2.setAmount(100);
    accounts.add(usd2);

    client.setAccounts(accounts);
    clientService.checkDuplicateCurrencies(client);
  }

  @Test
  public void testCheckCurrency_validCurrencyCode_fine() {
    String currencyCode = "AED";
    clientService.checkCurrency(currencyCode);
  }

  @Test(expected = AccountException.class)
  public void testCheckCurrency_invalidCurrencyCode_exception() {
    String currencyCode = "ZZZ";
    clientService.checkCurrency(currencyCode);
  }
}