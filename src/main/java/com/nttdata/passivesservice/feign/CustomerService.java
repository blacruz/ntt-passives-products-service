package com.nttdata.passivesservice.feign;

import reactor.core.publisher.Mono;

public interface CustomerService {

  public Mono<CustomerType> getCustomerTypeById(String objectId);
}
