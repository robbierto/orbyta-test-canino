package com.orbyta.banking.controller;

import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.account.AccountsPayload;
import com.orbyta.banking.model.balance.Balance;
import com.orbyta.banking.model.moneytransfer.MoneyTransferRequest;
import com.orbyta.banking.model.moneytransfer.MoneyTransferResponse;
import com.orbyta.banking.model.transaction.TransactionsPayload;
import com.orbyta.banking.service.FabrickService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private FabrickService fabrickService;

    @InjectMocks
    private AccountController accountController;

    @Mock
    private ApiResponse<AccountsPayload> accountsApiResponse;

    @Mock
    private ApiResponse<Balance> balanceApiResponse;

    @Mock
    private ApiResponse<TransactionsPayload> transactionsApiResponse;

    @Mock
    private ApiResponse<MoneyTransferResponse> moneyTransferApiResponse;

    private final String accountId = "14537780";

    /**
     * Test che verifica il metodo del controller per ottenere info dell'account.
     * 
     * Questo test verifica che:
     * - Il controller chiami il metodo corretto del service (getAccounts)
     * - Il controller restituisca la risposta fornita dal service
     * - Lo status HTTP della risposta sia 200 OK
     */
    @SuppressWarnings("unchecked")
    @Test
    void getAccounts_shouldReturnAccountsFromService() {
        // Given
        when(fabrickService.getAccounts()).thenReturn(accountsApiResponse);

        // When
        ResponseEntity<ApiResponse<AccountsPayload>> response = accountController.getAccounts();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountsApiResponse, response.getBody());
        verify(fabrickService).getAccounts();
    }

    /**
     * Test che verifica il metodo del controller per ottenere il saldo di
     * un account.
     * 
     * Questo test verifica che:
     * - Il controller chiami il metodo corretto del service (getAccountBalance)
     * con l'ID account corretto
     * - Il controller restituisca la risposta fornita dal service
     * - Lo status HTTP della risposta sia 200 OK
     */
    @SuppressWarnings("unchecked")
    @Test
    void getAccountBalance_shouldReturnBalanceFromService() {
        // Given
        when(fabrickService.getAccountBalance(accountId)).thenReturn(balanceApiResponse);

        // When
        ResponseEntity<ApiResponse<Balance>> response = accountController.getAccountBalance(accountId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balanceApiResponse, response.getBody());
        verify(fabrickService).getAccountBalance(accountId);
    }

    /**
     * Test che verifica il metodo del controller per ottenere le
     * transazioni di un account.
     * 
     * Questo test verifica che:
     * - Il controller chiami il metodo corretto del service
     * (getAccountTransactions)
     * con l'ID account e le date corrette
     * - Il controller restituisca la risposta fornita dal service
     * - Lo status HTTP della risposta sia 200 OK
     */
    @SuppressWarnings("unchecked")
    @Test
    void getAccountTransactions_shouldReturnTransactionsFromService() {
        // Given
        String fromDate = "2023-01-01";
        String toDate = "2023-01-31";

        when(fabrickService.getAccountTransactions(accountId, fromDate, toDate))
                .thenReturn(transactionsApiResponse);

        // When
        ResponseEntity<ApiResponse<TransactionsPayload>> response = accountController.getAccountTransactions(accountId,
                fromDate, toDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionsApiResponse, response.getBody());
        verify(fabrickService).getAccountTransactions(accountId, fromDate, toDate);
    }

    /**
     * Test che verifica il metodo del controller per creare un bonifico.
     * 
     * Questo test verifica che:
     * - Il controller chiami il metodo corretto del service (createMoneyTransfer)
     * con l'ID account e la richiesta di bonifico corretti
     * - Il controller restituisca la risposta fornita dal service
     * - Lo status HTTP della risposta sia 200 OK
     */
    @SuppressWarnings("unchecked")
    @Test
    void createMoneyTransfer_shouldReturnResponseFromService() {
        // Given
        MoneyTransferRequest request = createSampleMoneyTransferRequest();

        when(fabrickService.createMoneyTransfer(accountId, request))
                .thenReturn(moneyTransferApiResponse);

        // When
        ResponseEntity<ApiResponse<MoneyTransferResponse>> response = accountController.createMoneyTransfer(accountId,
                request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(moneyTransferApiResponse, response.getBody());
        verify(fabrickService).createMoneyTransfer(accountId, request);
    }

    private MoneyTransferRequest createSampleMoneyTransferRequest() {
        MoneyTransferRequest.Creditor creditor = new MoneyTransferRequest.Creditor();
        MoneyTransferRequest.Creditor.Account account = new MoneyTransferRequest.Creditor.Account();
        account.setAccountCode("IT60X0542811101000000123456");
        creditor.setName("John Doe");
        creditor.setAccount(account);

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setCreditor(creditor);
        request.setDescription("Test money transfer");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");

        return request;
    }
}
