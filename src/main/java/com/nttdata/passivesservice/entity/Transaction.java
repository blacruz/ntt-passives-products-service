package com.nttdata.passivesservice.entity;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.nttdata.passivesservice.service.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  
  @Id
  private String id;
  private Date date;
  private String accountId;
  private String agent;
//  private BigDecimal amount;
  private Double amount;
  private TransactionType type;

}
