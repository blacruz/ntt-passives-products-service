package com.nttdata.passivesservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.nttdata.passivesservice.dto.TransactionDto;
import com.nttdata.passivesservice.repository.TransactionRepositoryCustom;
import com.nttdata.passivesservice.service.AccountService;
import com.nttdata.passivesservice.service.TransactionType;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionTests {
  
  @Autowired
  private AccountService accountService;
  
  @Autowired
  private TransactionRepositoryCustom transactionRepositoryCustom;
  
  @BeforeAll
  public static void setup() {
    System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
  }

//  @Test
  public void createTransaction() {
    var tx = new TransactionDto();
    tx.setAgent("BCP La Molina");
    tx.setAccountId("1wqew45w4w");
    tx.setAmount(500d);
    tx.setType(TransactionType.DEPOSIT);
    
    var monoTransactionId = accountService.createTransaction(tx);
    StepVerifier.create(monoTransactionId).assertNext(acc -> {
    }).verifyComplete();
    
    var txW = new TransactionDto();
    txW.setAgent("BCP Santa Rosa");
    txW.setAccountId("1wqew45w4w");
    txW.setAmount(-100d);
    txW.setType(TransactionType.WITHDRAWAL);
    
    var monoTransactionWiId = accountService.createTransaction(txW);
    StepVerifier.create(monoTransactionWiId)
    .assertNext(acc -> {
    }).verifyComplete();
    
  }
  
  @Test
  public void getBalance() {
    var mono = transactionRepositoryCustom.getBalanceByAccount("11wqew45w4w");
    try {
    System.out.println(mono.block());
    } catch(Exception ex ) {
      System.out.println("eeeee");
    }
  }
}
