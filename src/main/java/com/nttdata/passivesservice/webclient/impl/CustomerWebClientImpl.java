package com.nttdata.passivesservice.webclient.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.nttdata.passivesservice.service.CustomerType;
import com.nttdata.passivesservice.webclient.CustomerWebClient;
import reactor.core.publisher.Mono;

@Component
public class CustomerWebClientImpl implements CustomerWebClient {

  @Override
  public Mono<CustomerType> getCustomerTypeById(String customerId) {

    var client = WebClient.builder().baseUrl("http://localhost:5000")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

    var customerType = client.get().uri("/customer/type/{id}", customerId).retrieve()
        .bodyToMono(CustomerType.class);
    return customerType;
  }

}
