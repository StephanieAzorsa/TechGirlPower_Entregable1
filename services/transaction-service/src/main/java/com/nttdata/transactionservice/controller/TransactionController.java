package com.nttdata.transactionservice.controller;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/withdraw")
    public Mono<ResponseEntity<TransactionResponseDTO>> registerWithdraw(
            @Validated @RequestBody TransactionRequestDTO request) {
        return transactionService.registerWithdrawal(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping("/transfer")
    public Mono<ResponseEntity<TransactionResponseDTO>> registerTransfer(
            @Validated @RequestBody TransferRequestDTO transferRequest) {

        return transactionService.registerTransfer(transferRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
