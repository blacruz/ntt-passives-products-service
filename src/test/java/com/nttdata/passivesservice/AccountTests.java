package com.nttdata.passivesservice;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.nttdata.passivesservice.common.AccountType;
import com.nttdata.passivesservice.feign.CustomerService;
import com.nttdata.passivesservice.feign.CustomerType;
import com.nttdata.passivesservice.service.AccountService;
import reactor.core.publisher.Flux;
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
  void createFixedTermAccount() {
    var customerCompanyId = "C001";
    var customerPersonId = "N001";
    var customerPersonId2 = "N002";

    when(customerService.getCustomerTypeById(customerCompanyId))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    when(customerService.getCustomerTypeById(customerPersonId))
        .thenReturn(Mono.just(CustomerType.PERSONAL));

    when(customerService.getCustomerTypeById(customerPersonId2))
        .thenReturn(Mono.just(CustomerType.PERSONAL));

    // Un cliente empresa no puede crear cuentas de plazo fijo
    var mono = accountService.createNewAccount(customerCompanyId, AccountType.FIXED_TERM);
    StepVerifier.create(mono).expectError(RuntimeException.class).verify();

    // Un cliente persona si puede crear cuentas de plazo fijo
    var newAccountPerson =
        accountService.createNewAccount(customerPersonId, AccountType.FIXED_TERM);
    StepVerifier.create(newAccountPerson).assertNext(acc -> {
    }).verifyComplete();

    // Pero no puede crear mas de una cuenta de plazo fijo por el mismo cliente
    var secondAccountPerson =
        accountService.createNewAccount(customerPersonId, AccountType.FIXED_TERM);
    StepVerifier.create(secondAccountPerson).expectError(RuntimeException.class).verify();

    // Otro cliente nuevo si deberia poder generar su cuenta
    var otherAccountPerson =
        accountService.createNewAccount(customerPersonId2, AccountType.FIXED_TERM);
    StepVerifier.create(otherAccountPerson).assertNext(acc -> {
    }).verifyComplete();
  }

  // @Test
  void createSavingAccount() {
    var customerCompanyId = "C001";
    var customerPersonId = "N001";

    when(customerService.getCustomerTypeById(customerCompanyId))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    // Un cliente empresa no puede crear cuentas de ahorros
    var mono = accountService.createNewAccount(customerCompanyId, AccountType.SAVING);
    StepVerifier.create(mono).expectError(RuntimeException.class).verify();

    // Un cliente persona si puede crear cuentas de ahorros, pero solo una cuenta
    var newAccountPerson = accountService.createNewAccount(customerPersonId, AccountType.SAVING);
    StepVerifier.create(newAccountPerson).verifyComplete();
  }

  // @Test
  void createCurrentAccount() {
    var customerCompanyId = "C001";
    var customerPersonId = "N001";

    when(customerService.getCustomerTypeById(customerCompanyId))
        .thenReturn(Mono.just(CustomerType.COMPANY));

    // Un cliente empresa puede crear 1 o muchas cuentas corrientes
    var flux = Flux.merge(accountService.createNewAccount(customerCompanyId, AccountType.CURRENT))
        .then(accountService.createNewAccount(customerCompanyId, AccountType.SAVING));
    StepVerifier.create(flux).verifyComplete();

    // Un cliente persona si puede crear 1 cuenta corriente
    var newAccountPerson = accountService.createNewAccount(customerPersonId, AccountType.CURRENT);
    StepVerifier.create(newAccountPerson).verifyComplete();
  }

}
