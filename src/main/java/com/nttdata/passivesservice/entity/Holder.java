package com.nttdata.passivesservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Holder {

  private String customerId;
  private Boolean main;
  private Boolean active;

  public Holder(String customerId) {
    this.customerId = customerId;
  }

}
