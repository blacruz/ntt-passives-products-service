package com.nttdata.passivesservice.service;

import lombok.Getter;

public enum TransactionType {

  WITHDRAWAL(-1), DEPOSIT(1);
  
  @Getter
  private Integer factor;
  
  private TransactionType(Integer factor) {
    this.factor = factor;
  }
}
