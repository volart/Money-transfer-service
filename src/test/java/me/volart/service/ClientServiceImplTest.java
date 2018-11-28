package me.volart.service;

import lombok.extern.slf4j.Slf4j;
import me.volart.dao.InMemoryClientDao;
import me.volart.dao.model.ClientDto;
import me.volart.dto.Account;
import me.volart.dto.Client;
import me.volart.dto.TransferInfo;
import me.volart.exception.AccountException;
import me.volart.exception.ClientNotFound;
import me.volart.util.DataGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ClientServiceImplTest {

  private ClientServiceImpl clientService;

  private InMemoryClientDao clientDao = mock(InMemoryClientDao.class);

  @Before
  public void setUp() {
    clientService = new ClientServiceImpl(clientDao);
  }

  @Test
  public void testTransfer_enoughMoneyAndAccountExists_noException() {
    Long transferAmount = 1000L;
    String currency = "USD";
    ClientDto from = DataGenerator.createClientDto(1, currency, transferAmount);
    ClientDto to = DataGenerator.createClientDto(2, currency, 0);

    Long expectedToAmount = to.getAccounts().get(currency) + transferAmount;
    Long expectedFromAmount = from.getAccounts().get(currency) - transferAmount;

    TransferInfo transferInfo = DataGenerator.createTransferInfo(2, currency, transferAmount);

    clientService.transfer(from, to, transferInfo);
    assertEquals(expectedToAmount, to.getAccounts().get(currency));
    assertEquals(expectedFromAmount, from.getAccounts().get(currency));
  }

  @Test(expected = AccountException.class)
  public void testTransfer_notEnoughMoneyAndAccountExists_exception() {
    Long transferAmount = 10000L;
    String currency = "USD";
    ClientDto from = DataGenerator.createClientDto(1, currency, transferAmount - 1);
    ClientDto to = DataGenerator.createClientDto(2, currency, 0);
    TransferInfo transferInfo = DataGenerator.createTransferInfo(2, currency, transferAmount);
    clientService.transfer(from, to, transferInfo);
  }

  @Test
  public void testTransfer_enoughMoneyAndRecipientAccountDoesNotExist_noException() {
    Long transferAmount = 1000L;
    String usd = "USD";
    String rub = "RUB";
    ClientDto from = DataGenerator.createClientDto(1, usd, transferAmount);
    ClientDto to = DataGenerator.createClientDto(2, rub, 0);
    Long expectedFromAmount = from.getAccounts().get(usd) - transferAmount;
    TransferInfo transferInfo = DataGenerator.createTransferInfo(2, usd, transferAmount);

    clientService.transfer(from, to, transferInfo);
    assertEquals(transferAmount, to.getAccounts().get(usd));
    assertEquals(expectedFromAmount, from.getAccounts().get(usd));
  }

  @Test(expected = AccountException.class)
  public void testTransfer_senderAccountDoesNotExist_exception() {
    Long transferAmount = 1000L;
    String usd = "USD";
    String rub = "RUB";
    ClientDto from = DataGenerator.createClientDto(1, rub, transferAmount);
    ClientDto to = DataGenerator.createClientDto(2, usd, 0);
    TransferInfo transferInfo = DataGenerator.createTransferInfo(2, usd, transferAmount);

    clientService.transfer(from, to, transferInfo);
  }

  @Test
  public void testGetClientBy_clientExists_noException() {
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
  public void testGetAccount_accountExists_noException() {
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
  public void testCheckExistence_doesNotExist_noException() {
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
  public void testCheckDuplicateCurrencies_noDuplicates_noException() {
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
  public void testCheckCurrency_validCurrencyCode_noException() {
    String currencyCode = "AED";
    clientService.checkCurrency(currencyCode);
  }

  @Test(expected = AccountException.class)
  public void testCheckCurrency_invalidCurrencyCode_exception() {
    String currencyCode = "ZZZ";
    clientService.checkCurrency(currencyCode);
  }
}