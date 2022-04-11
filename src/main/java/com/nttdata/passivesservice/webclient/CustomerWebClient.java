package com.nttdata.passivesservice.webclient;

import com.nttdata.passivesservice.service.CustomerType;
import reactor.core.publisher.Mono;

public interface CustomerWebClient {

  public Mono<CustomerType> getCustomerTypeById(String customerId);
}
