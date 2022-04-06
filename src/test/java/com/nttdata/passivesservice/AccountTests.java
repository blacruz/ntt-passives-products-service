package com.nttdata.passivesservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.entity.Holder;
import com.nttdata.passivesservice.feign.CustomerService;
import com.nttdata.passivesservice.feign.CustomerType;
import com.nttdata.passivesservice.service.AccountService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class AccountTests {

  @Autowired
  private AccountService accountService;

  @MockBean
  private CustomerService customerService;

  @BeforeAll
  public static void setup() {
    System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
  }

  @Test
  void createCompanyWithPlazoFijoAccount() {
    when(customerService.getCustomerTypeById("ee1234-554515e-re3432"))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    var account = new Account();
    account.setAccountNumber("3311114587123");
    account.setState("ok");
    account.setType("plazo fijo");
    var holders = List.of(new Holder("ee1234-554515e-re3432", true, true));
    account.setHolders(holders);

    assertThrows(RuntimeException.class, () -> {
      accountService.save(account).block();
    });
  }

  @Test
  void createCompanyWithAhorroAccount() {
    when(customerService.getCustomerTypeById("ee1234-554515e-re3432"))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    var account = new Account();
    account.setAccountNumber("3311114587123");
    account.setState("ok");
    account.setType("ahorro");
    var holders = List.of(new Holder("ee1234-554515e-re3432", true, true));
    account.setHolders(holders);

    assertThrows(RuntimeException.class, () -> {
      accountService.save(account).block();
    });
  }

  @Test
  void createCompanyWithSingleCorrienteAccount() {
    when(customerService.getCustomerTypeById("ee1234-554515e-re3432"))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    var account = new Account();
    account.setAccountNumber("3311114587123");
    account.setState("ok");
    account.setType("corriente");
    var holders = List.of(new Holder("ee1234-554515e-re3432", true, true));
    account.setHolders(holders);

    var savedAccount = accountService.save(account).block();
    assertNotNull(savedAccount);
  }
  
  @Test
  void createCompanyWithMultipleCorrienteAccount() {
    when(customerService.getCustomerTypeById("ee1234-554515e-re3432"))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    var account = new Account();
    account.setAccountNumber("3311114587123");
    account.setState("ok");
    account.setType("corriente");
    var holders = List.of(new Holder("ee1234-554515e-re3432", true, true));
    account.setHolders(holders);

    var savedAccount = accountService.save(account).block();
    assertNotNull(savedAccount);
    
    var account2 = new Account();
    account2.setAccountNumber("3311114587123");
    account2.setState("ok");
    account2.setType("corriente");
    var holders2 = List.of(new Holder("ee1234-554515e-re3432", true, true));
    account2.setHolders(holders2);

    var savedAccount2 = accountService.save(account2).block();
    assertNotNull(savedAccount2);
  }

}
