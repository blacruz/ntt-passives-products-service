package com.nttdata.passivesservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.nttdata.passivesservice.common.AccountState;
import com.nttdata.passivesservice.common.AccountType;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.entity.BalanceDTO;
import com.nttdata.passivesservice.entity.Holder;
import com.nttdata.passivesservice.repository.AccountRepository;
import com.nttdata.passivesservice.service.rules.AccountRule;
import com.nttdata.passivesservice.service.rules.AccountValidator;
import com.nttdata.passivesservice.service.rules.Rules;
import com.nttdata.passivesservice.webclient.CustomerWebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

  private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CustomerWebClient customerService;

  private Mono<Account> validateAccountPersist(Account account) {

    List<Holder> holders = account.getHolders();
    Holder mainHolder =
        holders.stream().filter(h -> h.getActive() && h.getMain()).findFirst().get();

    if (CollectionUtils.isEmpty(holders))
      return Mono.error(new RuntimeException("Holders can not be empty"));

    var validator = AccountValidator.empty
        .and(AccountValidator.companyHasNotFixedTermAccount)
        .and(AccountValidator.companyHasNotSavingAccount)
        .and(accountRule -> {
          
          var filter = new Account();
          filter.setType(account.getType());
          filter.setHolders(List.of(new Holder(mainHolder.getCustomerId())));

          var ruleSet = accountRepository.findAll(Example.of(filter)).count()
              .map(count -> {
                
                var hasAccounts = (Predicate<AccountRule>) t -> count > 0;
                
                var rule = AccountValidator.rule(
                    hasAccounts.and(AccountValidator.isNatural), 
                    Rules.NATURAL_HAS_NOT_MORE_THAN_ONE_ACCOUNT_OF_SAME_TYPE);
                
                return rule.apply(accountRule);
              });
          return ruleSet.block();
        });
    
    var accountResult = customerService.getCustomerTypeById(mainHolder.getCustomerId())
      .map(customerType -> {
        var validaciones = validator.apply(new AccountRule(account, customerType));
        return account;
      })
      ;

    return null;
//    var response =
//        customerType.flatMap(type -> Flux.fromIterable().next().switchIfEmpty(Mono.just(account)))
//            .flatMap(r -> Mono.error(new RuntimeException(r.getMsg())));
//
//    return response;

  }

  /**
   * Crea una nueva cuenta de pasivo
   * 
   * @param customerId
   * @param accountType
   * @return
   */
  public Mono<Account> createNewAccount(String customerId, AccountType accountType) {

    var holder = new Holder();
    holder.setCustomerId(customerId);
    holder.setActive(true);
    holder.setMain(true);

    var account = new Account();
    account.setHolders(List.of(holder));
    account.setType(accountType);
    account.setState(AccountState.OK);

    var v = validateAccountPersist(account);
    
    v.map(acc -> {
      var x = accountRepository.findFirst1ByOrderByAccountNumberDesc()
          .map(Account::getAccountNumber)
          .defaultIfEmpty(0)
          .map(lastNumber -> {
            acc.setAccountNumber(lastNumber + 1);
            var saved = accountRepository.save(acc);
            var savedMapped = saved.map(savedAccount -> {
              logger.info(String.format("Account created with id {0}", savedAccount.getId()));
              return savedAccount;
            });
            return saved;
          });
      return x;
    });
    return v;
  }

  public Flux<BalanceDTO> balanceByCustomer(String customerId) {
    return Flux.just(new BalanceDTO(AccountType.FIXED_TERM, new BigDecimal(1500)), new BalanceDTO(AccountType.SAVING, new BigDecimal(4000)));
  }

}
