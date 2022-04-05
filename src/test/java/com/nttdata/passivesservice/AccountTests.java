package com.nttdata.passivesservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.repository.AccountRepository;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class AccountTests {
  
  @Autowired
  private AccountRepository accountRepository;
  
  @BeforeAll
  public static void setup() {
    System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
  }

  @Test
  void createPersonalAccount() {
    var account = new Account();
    account.setAccountNumber("3311114587123");
    account.setState("ok");
    account.setType("ahorro");
    
    accountRepository.save(account).block();
    
    var accountDb = accountRepository.findByAccountNumber("3311114587123");
    StepVerifier.create(accountDb)
      .assertNext(acc -> {
        assertEquals("ahorro", acc.getType());
      })
      .expectComplete()
      .verify();
    
  }

}
