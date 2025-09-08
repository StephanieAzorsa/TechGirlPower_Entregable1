package com.nttdata.transactionservice.client;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.exception.AccountNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountWebClient {

    private final WebClient webClient;

    public AccountWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082/api/v1/accounts").build();
    }

    public Mono<Account> depositBalanceAccount(TransactionRequestDTO transactionRequest) {
        return webClient
                .put()
                .uri("/{account}/deposit", transactionRequest.getAccountId())
                .bodyValue(transactionRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Error al realizar depósito en cuenta: "
                                + transactionRequest.getAccountId())))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error del servidor al realizar depósito")))
                .bodyToMono(Account.class);
    }

    public Mono<Account> withdrawBalanceAccount(TransactionRequestDTO transactionRequest) {
        return webClient
                .put()
                .uri("/{account}/withdraw", transactionRequest.getAccountId())
                .bodyValue(transactionRequest)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Error al realizar retiro en cuenta: "
                                + transactionRequest.getAccountId())))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error del servidor al realizar depósito")))
                .bodyToMono(Account.class);
    }

    public Mono<Account> getAccountById(String accountId) {
        return webClient
                .get()
                .uri("/{accountId}", accountId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response ->
                                Mono.error(new AccountNotFoundException("Cuenta no encontrada: " + accountId)))
                .onStatus(status -> status.is5xxServerError(),
                        response ->
                                Mono.error(new RuntimeException("Error del servidor al obtener cuenta: " + accountId)))
                .bodyToMono(Account.class);
    }



}
