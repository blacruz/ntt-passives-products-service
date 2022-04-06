package com.nttdata.passivesservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Holder {
  
  @Id
  private String customerId;
  private Boolean main;
  private Boolean active;
  
}
