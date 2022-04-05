package com.nttdata.passivesservice.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document("Accounts")
@Data
public class Account {
  
  @Id
  private ObjectId id;
  
  private String accountNumber;
  private String type;
  @DBRef private List<Movement> movements;
  private String state;
  @DBRef private List<Holder> holders;
  @DBRef private List<AuthSigner> authSigners;

}
