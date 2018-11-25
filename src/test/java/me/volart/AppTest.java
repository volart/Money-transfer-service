package me.volart;

import me.volart.dao.model.ClientDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
public class AppTest {

  @Test
  public void curTest() {
    Long expected = new Long(11);
    Long actual = new Long(11);
    assertEquals(expected, actual);
    assertTrue(expected.hashCode() - actual.hashCode() == 0);
  }


  Executor executor = Executors.newFixedThreadPool(10);

  @Test
  public void test() {
    int n = 3;
    List<ClientDto> clientDtos = new ArrayList<>(n);
    for (int i = 1; i <= n; i++) {
      ClientDto clientDto = new ClientDto();
      clientDto.setId((long) i);
      clientDtos.add(clientDto);
    }
    for (int i = 0; i < 100; i++) {
      int j = i;
      executor.execute(() -> {
        f(clientDtos.get(j % n), clientDtos.get((j + 1) % n));
      });
    }
  }

  private void f(ClientDto c1, ClientDto c2) {
    ClientDto from = c1;
    ClientDto to = c2;

    Long fromId = from.getId();
    Long toId = to.getId();
    System.out.println("fromId = " + fromId);
    System.out.println("toId = " + toId);
    if (fromId > toId) {
      synchronized (fromId) {
        System.out.println("fromId locked 1 -> " + fromId);
        synchronized (toId) {
          System.out.println("toId locked 2 -> " + toId);
        }
      }
    } else {
      synchronized (toId) {
        System.out.println("toId locked 1 -> " + toId);
        synchronized (fromId) {
          System.out.println("fromId locked 2 -> " + fromId);
        }
      }
    }

  }
}

