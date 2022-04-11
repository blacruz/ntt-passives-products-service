package com.nttdata.passivesservice.dto;

import com.nttdata.passivesservice.common.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {

//  private String accountId;
  private AccountType accountType;
  private Double balance;
  
}
