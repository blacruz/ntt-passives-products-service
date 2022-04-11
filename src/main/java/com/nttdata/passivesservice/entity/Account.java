package com.nttdata.passivesservice.entity;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.nttdata.passivesservice.common.AccountState;
import com.nttdata.passivesservice.common.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  
  @Id
  private String id;
  
  private Integer accountNumber;
  private AccountType type;
  private AccountState state;
  private List<Holder> holders;
  @DBRef private List<AuthSigner> authSigners;

}
