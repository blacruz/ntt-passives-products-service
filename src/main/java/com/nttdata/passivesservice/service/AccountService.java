package com.nttdata.passivesservice.service;

import java.math.BigDecimal;
import java.util.Date;
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
import com.nttdata.passivesservice.dto.BalanceDto;
import com.nttdata.passivesservice.dto.TransactionDto;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.entity.Holder;
import com.nttdata.passivesservice.entity.Transaction;
import com.nttdata.passivesservice.repository.AccountRepository;
import com.nttdata.passivesservice.repository.TransactionRepository;
import com.nttdata.passivesservice.repository.TransactionRepositoryCustom;
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
  private TransactionRepository transactionRepository; 

  @Autowired
  private CustomerWebClient customerService;
  
  @Autowired
  private TransactionRepositoryCustom transactionRepositoryCustom;

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
        .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")))
        .handle((customerType, sink) -> {
          var msg = new StringBuilder();
          validator.apply(new AccountRule(account, customerType)).stream()
              .forEach(val -> {
                msg.append(val + "");
              });
          if (msg.length() > 0) {
            sink.error(new RuntimeException(msg.toString()));
          } else {
            sink.next(customerType);
          }
        })
      .map(customerType -> {
        return account;
      })
      ;

    return accountResult;
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
    
    return validateAccountPersist(account)
      .flatMap(acc -> {
        
        var accountDefaultZero = new Account();
        accountDefaultZero.setAccountNumber(0);
        
        return accountRepository.findFirst1ByOrderByAccountNumberDesc()
            .defaultIfEmpty(accountDefaultZero)
            .map(accLn -> accLn.getAccountNumber() + 1)
            .flatMap(newAccountNumber -> {
              account.setAccountNumber(newAccountNumber);
              return accountRepository.save(account)
                  .map(accountSaved -> {
                    logger.info(String.format("Account created with id {0}", accountSaved.getId()));
                    return accountSaved;
                  });
            })
            ;
        
      })
      ;

//    var v = validateAccountPersist(account);
//    
//    v.map(acc -> {
//      var x = accountRepository.findFirst1ByOrderByAccountNumberDesc()
//          .map(Account::getAccountNumber)
//          .defaultIfEmpty(0)
//          .map(lastNumber -> {
//            acc.setAccountNumber(lastNumber + 1);
//            var saved = accountRepository.save(acc);
//            var savedMapped = saved.map(savedAccount -> {
//              logger.info(String.format("Account created with id {0}", savedAccount.getId()));
//              return savedAccount;
//            });
//            return saved;
//          });
//      return x;
//    });
//    return v;
  }

  public Flux<BalanceDto> balanceByCustomer(String customerId) {
    var accountFilter = new Account();
    var holder = new Holder(customerId, true, true);
    accountFilter.setHolders(List.of(holder));
    var example = Example.of(accountFilter);
    var accounts = accountRepository.findAll(example)
        .flatMap(acc -> transactionRepositoryCustom.getBalanceByAccounts(acc.getId()));
    return accounts;
  }

  public Mono<String> createTransaction(TransactionDto dto) {
    var depositCanNotNegative = (Predicate<TransactionDto>) obj -> obj.getType().getFactor() < 0 && obj.getAmount() > 0;
    var withadrawCanNotPositive = (Predicate<TransactionDto>) obj -> obj.getType().getFactor() > 1 && obj.getAmount() < 0;
    var txNoZeroAmoun = (Predicate<TransactionDto>) obj -> obj.getAmount() == 0;
//    var withadrawLessEqToBalance = (Predicate<TransactionDto>) obj -> {
//      transactionRepository
//    };
    
    var transaction = new Transaction();
    transaction.setAccountId(dto.getAccountId());
    transaction.setDate(new Date());
    transaction.setType(dto.getType());
    transaction.setAmount(dto.getAmount());
//    transaction.setFactor(dto.getType().getFactor());
    transaction.setAgent(dto.getAgent());
    var monoTx = transactionRepository.save(transaction);
    return monoTx.map(tx -> tx.getAccountId());
  }

}
