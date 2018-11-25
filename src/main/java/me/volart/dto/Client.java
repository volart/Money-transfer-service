package me.volart.dto;

import lombok.Data;

import java.util.List;

@Data
public class Client {

  /**
   * Identifier
   */
  private long id;

  /**
   * List of accounts. There shouldn't be accounts with similar currency.
   */
  private List<Account> accounts;
}
