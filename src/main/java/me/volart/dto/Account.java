package me.volart.dto;

import lombok.Data;

@Data
public class Account {
  /**
   * Money amount
   */
  private long amount;

  /**
   * ISO 4217 Currency Code
   */
  private String currency;
}
