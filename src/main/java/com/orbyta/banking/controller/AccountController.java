package com.orbyta.banking.controller;

import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.AccountsPayload;
import com.orbyta.banking.model.Balance;
import com.orbyta.banking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccountsPayload>> getAccounts() {
        ApiResponse<AccountsPayload> response = accountService.getAccounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<Balance>> getAccountBalance(@PathVariable String accountId) {
        ApiResponse<Balance> response = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(response);
    }
}
