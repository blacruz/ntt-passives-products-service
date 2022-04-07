package com.nttdata.passivesservice.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.passivesservice.entity.Account;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, ObjectId> {
  
  public Mono<Account> findByAccountNumber(String accountNumber);

  public Mono<Account> findFirst1ByOrderByAccountNumber();

}
