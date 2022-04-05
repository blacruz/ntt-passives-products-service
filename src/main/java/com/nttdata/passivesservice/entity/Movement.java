package com.nttdata.passivesservice.entity;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document
@Data
public class Movement {

  @Id
  private ObjectId id;
  
  private Date date;
  private String type;
  private String customerId;
  private String agent;
  private Float amount;
  private Integer factor;
  private Integer operationNumber;
}
