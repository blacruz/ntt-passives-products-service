package com.nttdata.passivesservice.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.passivesservice.entity.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, ObjectId> {
  
  public Mono<Account> findByAccountNumber(Integer accountNumber);

  public Mono<Account> findFirst1ByOrderByAccountNumberDesc();

//  public Flux<Account> findAllByCustomerId(String customerId);
  
}
