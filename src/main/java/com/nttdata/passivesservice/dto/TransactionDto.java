package com.nttdata.passivesservice.dto;

import com.nttdata.passivesservice.service.TransactionType;
import lombok.Data;

@Data
public class TransactionDto {

  private String accountId;
  private TransactionType type;
  private String agent;
//  private BigDecimal amount;
  private Double amount;
}
