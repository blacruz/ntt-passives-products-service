package com.nttdata.passivesservice.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.nttdata.passivesservice.common.AccountState;
import com.nttdata.passivesservice.common.AccountType;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.entity.Holder;
import com.nttdata.passivesservice.feign.CustomerService;
import com.nttdata.passivesservice.feign.CustomerType;
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
      return Mono.error(new RuntimeException("Holders can not be empty"));



    return customerService.getCustomerTypeById(mainHolder.getCustomerId()).flatMap(customerType -> {
      if (CustomerType.COMPANY == customerType && account.getType() == AccountType.FIXED_TERM) {
        return Mono
            .error(new RuntimeException("Company can not take account with fixed term type"));
      }
      if (CustomerType.COMPANY == customerType && account.getType() == AccountType.SAVING) {
        return Mono.error(new RuntimeException("Company can not take account with Saving type"));
      }
      if (CustomerType.PERSONAL == customerType) {
        var accountExample = Example.of(Account.builder().type(account.getType())
            .holders(List.of(Holder.builder().customerId(mainHolder.getCustomerId()).build()))
            .build());
        return accountRepository.findAll(accountExample).hasElements()
            .flatMap(hasElements -> hasElements
                ? Mono.error(new RuntimeException(String.format(
                    "Personal account can not has more 1 account type {0}", account.getType())))
                : Mono.just(account));
      }
      return Mono.just(account);
    });


  }

  /**
   * Crea una nueva cuenta de pasivo
   * 
   * @param customerId
   * @param accountType
   * @return
   */
  public Mono<Account> createNewAccount(String customerId, AccountType accountType) {

    var holder = Holder.builder().customerId(customerId).active(true).main(true).build();

    var account =
        Account.builder().holders(List.of(holder)).type(accountType).state(AccountState.OK).build();

    return validateAccountPersist(account).map(acc -> {
      var lastAccountNumber = accountRepository.findFirst1ByOrderByAccountNumberDesc()
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
