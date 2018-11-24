package me.volart.dto;

import lombok.Data;

@Data
public class TransferInfo {

  /**
   * Recipient client id
   */
  private long clientId;

  /**
   * Money transfer amount
   */
  private long amount;

  /**
   * ISO 4217 Currency Code
   */
  private String currency;
}
