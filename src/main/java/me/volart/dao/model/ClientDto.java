package me.volart.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class ClientDto {

  private Long id;
  private List<AccountDto> accounts;

  @EqualsAndHashCode.Exclude @ToString.Exclude
  private final Object syncObj = new Object();

}
