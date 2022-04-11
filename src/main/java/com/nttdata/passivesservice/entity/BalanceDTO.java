package com.nttdata.passivesservice.entity;

import java.math.BigDecimal;
import com.nttdata.passivesservice.common.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDTO {

  private AccountType accountType;
  private BigDecimal balance;
  
}
