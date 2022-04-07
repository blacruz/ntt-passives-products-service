package com.nttdata.passivesservice.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.nttdata.passivesservice.service.AccountState;
import com.nttdata.passivesservice.service.AccountType;
import lombok.Builder;
import lombok.Data;

@Document("Accounts")
@Data
@Builder
public class Account {
  
  @Id
  private ObjectId id;
  
  private Integer accountNumber;
  private AccountType type;
  @DBRef private List<Movement> movements;
  private AccountState state;
  @DBRef private List<Holder> holders;
  @DBRef private List<AuthSigner> authSigners;

}
