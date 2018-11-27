package me.volart.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
public class ClientDto {

  private Long id;
  private Map<String, Long> accounts;

  @EqualsAndHashCode.Exclude @ToString.Exclude
  private final Object syncObj = new Object();

}
