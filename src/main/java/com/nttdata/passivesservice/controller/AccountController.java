package com.nttdata.passivesservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.passivesservice.dto.CreateAccountDto;
import com.nttdata.passivesservice.entity.Account;
import com.nttdata.passivesservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
  
  private final AccountService accountService;
  
  @PostMapping
  public Mono<Account> createNewAccount(@RequestBody CreateAccountDto dto) {
    var mono = accountService.createNewAccount(dto.getCustomerId(), dto.getAccountType());
    return mono;
  }

}