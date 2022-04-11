package com.nttdata.passivesservice.service.rules;

public enum Rules {

  COMPANY_CAN_NOT_HAVE_FIXED_TERM_ACCOUNT("Company customer can not have fixed term account"),
  COMPANY_CAN_NOT_HAVE_SAVING_ACCOUNT("Company customer can not have saving account"),
  NATURAL_HAS_NOT_MORE_THAN_ONE_ACCOUNT_OF_SAME_TYPE("Natural customer can not have more than 1 account of same type");

  private String str;

  private Rules(String str) {
    this.str = str;
  }
  
  public String getMsg() {
    return str;
  }
}
