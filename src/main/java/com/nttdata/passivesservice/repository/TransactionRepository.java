package com.nttdata.passivesservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.nttdata.passivesservice.entity.Transaction;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

  Flux<Transaction> findAllByAccountId(String accountId);
  
}
