package com.nttdata.transactionservice.client;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountWebClient {

    private final WebClient webClient;

    public AccountWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8082/api/v1/accounts")
                .build();
    }

    public Mono<Account> depositBalanceAccount(TransactionRequestDTO transactionRequest) {
        return webClient
                .put()
                .uri("/{account}/deposit", transactionRequest.getAccountId())
                .bodyValue(transactionRequest)
                .retrieve()
                .bodyToMono(Account.class);
    }

    public Mono<Account> withdrawBalanceAccount(TransactionRequestDTO transactionRequest) {
        return webClient
                .put()
                .uri("/{account}/withdraw", transactionRequest.getAccountId())
                .bodyValue(transactionRequest)
                .retrieve()
                .bodyToMono(Account.class);
    }

    public Mono<Account> getAccountById(String accountId) {
        return webClient
                .get()
                .uri("/{accountId}", accountId)
                .retrieve()
                .bodyToMono(Account.class);
    }

}
