package com.nttdata.passivesservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.feign.CustomerService;
import com.nttdata.passivesservice.feign.CustomerType;
import com.nttdata.passivesservice.repository.AccountRepository;
import reactor.core.publisher.Mono;

@Service
public class AccountService {
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private CustomerService customerService;

  public Mono<Account> save(Account account) {
    if (account.getHolders() == null)
      throw new RuntimeException("Holders is required");
    var holder = account.getHolders().stream()
        .filter(acc -> acc.getActive() && acc.getMain())
        .findFirst()
        .get();
    var customerType = customerService.getCustomerTypeById(holder.getCustomerId()).block();
    if (customerType == CustomerType.COMPANY && "ahorro".equals(account.getType())) {
      throw new RuntimeException("Customer type company can not create a AHORRO account");
    }
    if (customerType == CustomerType.COMPANY && "plazo fijo".equals(account.getType())) {
      throw new RuntimeException("Customer type company can not create a PLAZO FIJO account");
    }
    var savedAccound = accountRepository.findByAccountNumber(account.getAccountNumber()).block();
    
    return accountRepository.save(account);
  }
  
  

}
