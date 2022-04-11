package com.nttdata.passivesservice.repository;

import com.nttdata.passivesservice.dto.BalanceDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepositoryCustom {

  public Mono<BalanceDto> getBalanceByAccount(String accountId);

  public Flux<BalanceDto> getBalanceByAccounts(String... accountsId);
}
