package com.nttdata.passivesservice.service.rules;

import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.service.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRule {
  
  private Account account;
  private CustomerType customerType;

}
