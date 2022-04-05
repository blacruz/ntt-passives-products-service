package com.nttdata.passivesservice.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document
@Data
public class AuthSigner {

  private String name;
  private String sign;
  private String dni;
  private Boolean active;
}
