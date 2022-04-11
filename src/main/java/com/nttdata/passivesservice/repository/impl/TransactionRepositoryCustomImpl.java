package com.nttdata.passivesservice.repository.impl;

import java.util.NoSuchElementException;
import org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi.withSha224;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import com.nttdata.passivesservice.dto.BalanceDto;
import com.nttdata.passivesservice.entity.Transaction;
import com.nttdata.passivesservice.repository.TransactionRepositoryCustom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

  @Autowired
  private ReactiveMongoTemplate mongoTemplate;
  
  @Override
  public Mono<BalanceDto> getBalanceByAccount(String accountId) {
    var agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("accountId").is(accountId)), 
        Aggregation.group("accountId").sum("amount").as("balance"));
    
    var results = mongoTemplate.aggregate(agg, Transaction.class, BalanceDto.class);
    
    var data = results.single();
    return data;
  }

  @Override
  public Flux<BalanceDto> getBalanceByAccounts(String... accountsId) {
    var agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("accountId").in(accountsId)), 
        Aggregation.group("accountId").sum("amount").as("balance"));
    
    var results = mongoTemplate.aggregate(agg, Transaction.class, BalanceDto.class);
    
    var data = results;
    return data;
  }

}
