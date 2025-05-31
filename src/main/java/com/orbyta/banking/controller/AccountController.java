package com.orbyta.banking.controller;

import com.orbyta.banking.constants.ApiConstants;
import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.account.AccountsPayload;
import com.orbyta.banking.model.balance.Balance;
import com.orbyta.banking.model.moneytransfer.MoneyTransferRequest;
import com.orbyta.banking.model.moneytransfer.MoneyTransferResponse;
import com.orbyta.banking.model.transaction.TransactionsPayload;
import com.orbyta.banking.service.FabrickService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_ACCOUNTS_BASE_PATH)
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final FabrickService fabrickService;

    public AccountController(FabrickService accountService) {
        this.fabrickService = accountService;
        logger.info("AccountController initialized");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccountsPayload>> getAccounts() {
        logger.info("Received request to get account information");
        ApiResponse<AccountsPayload> response = fabrickService.getAccounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}" + ApiConstants.BALANCE_ENDPOINT)
    public ResponseEntity<ApiResponse<Balance>> getAccountBalance(@PathVariable String accountId) {
        logger.info("Received request to get balance for account: {}", accountId);
        ApiResponse<Balance> response = fabrickService.getAccountBalance(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}" + ApiConstants.TRANSACTIONS_ENDPOINT)
    public ResponseEntity<ApiResponse<TransactionsPayload>> getAccountTransactions(
            @PathVariable String accountId,
            @RequestParam(required = true) String fromAccountingDate,
            @RequestParam(required = true) String toAccountingDate) {

        logger.info("Received request to get transactions for account: {} from: {} to: {}",
                accountId, fromAccountingDate, toAccountingDate);

        ApiResponse<TransactionsPayload> response = fabrickService.getAccountTransactions(
                accountId, fromAccountingDate, toAccountingDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}" + ApiConstants.MONEY_TRANSFERS_ENDPOINT)
    public ResponseEntity<ApiResponse<MoneyTransferResponse>> createMoneyTransfer(
            @PathVariable String accountId,
            @RequestBody @Valid MoneyTransferRequest request) {

        logger.info("Received request to create money transfer for account: {} with amount: {} {}",
                accountId, request.getAmount(), request.getCurrency());

        ApiResponse<MoneyTransferResponse> response = fabrickService.createMoneyTransfer(accountId, request);
        return ResponseEntity.ok(response);
    }
}
