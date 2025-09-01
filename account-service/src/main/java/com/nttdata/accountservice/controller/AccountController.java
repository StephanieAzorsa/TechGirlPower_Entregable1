package com.nttdata.accountservice.controller;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid
            @RequestBody AccountRequestDTO accountRequestDTO) {

        AccountResponseDTO newAccount = accountService
                .createAccount(accountRequestDTO);

        return ResponseEntity.ok(newAccount);
    }

}
