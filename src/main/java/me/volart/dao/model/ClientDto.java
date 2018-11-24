package me.volart.dao.model;

import lombok.Data;

import java.util.List;

@Data
public class ClientDto {
  private long id;
  private List<AccountDto> accounts;
}
