package com.nttdata.passivesservice.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.entity.Holder;
import com.nttdata.passivesservice.feign.CustomerService;
import com.nttdata.passivesservice.repository.AccountRepository;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

  private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CustomerService customerService;

  private Mono<Account> validateAccountPersist(Account account) {
    var holders = account.getHolders();
    var mainHolder = holders.stream().filter(h -> h.getActive() && h.getMain()).findFirst().get();

    if (CollectionUtils.isEmpty(holders))
      throw new RuntimeException("Holders can not be empty");

    var customerType = customerService.getCustomerTypeById(mainHolder.getCustomerId()).block();
    
    

    return Mono.just(account);
  }

  public Mono<Account> createNewAccount(String customerId, AccountType accountType) {
    
    var holder = Holder.builder().customerId(customerId).active(true).main(true).build();

    var account = Account.builder().holders(List.of(holder)).type(accountType)
        .state(AccountState.OK).build();

    return validateAccountPersist(account).map(acc -> {
      var lastAccountNumber = accountRepository.findFirst1ByOrderByAccountNumber()
          .defaultIfEmpty(Account.builder().accountNumber(0).build())
          .map(lac -> lac.getAccountNumber()).block();
      acc.setAccountNumber(lastAccountNumber + 1);
      return acc;
    }).flatMap(acc -> accountRepository.save(acc).map(savedAccount -> {
      logger.info(String.format("Account created with id {0}", savedAccount.getId()));
      return savedAccount;
    }));
  }



}
