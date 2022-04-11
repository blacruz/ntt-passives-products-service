package com.nttdata.passivesservice.dto;

import com.nttdata.passivesservice.common.AccountType;
import lombok.Data;

@Data
public class CreateAccountDto {

  private String customerId;
  private AccountType accountType;
}
