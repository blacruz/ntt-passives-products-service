package com.nttdata.passivesservice.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document
@Data
public class Holder {
  
  private String customerId;
  private Boolean main;
  private Boolean active;
  
}
