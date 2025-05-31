package com.orbyta.banking.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyta.banking.constants.ApiConstants;
import com.orbyta.banking.controller.AccountController;
import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.account.AccountsPayload;
import com.orbyta.banking.model.balance.Balance;
import com.orbyta.banking.model.moneytransfer.MoneyTransferRequest;
import com.orbyta.banking.model.moneytransfer.MoneyTransferResponse;
import com.orbyta.banking.model.transaction.TransactionsPayload;
import com.orbyta.banking.service.FabrickService;

@WebMvcTest(AccountController.class)
public class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FabrickService fabrickService;

    @MockBean
    private RestTemplate restTemplate;

    private final String accountId = "14537780";

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        // imposto mock Response
        ApiResponse<AccountsPayload> accountsResponse = new ApiResponse<>();
        accountsResponse.setStatus(ApiConstants.STATUS_OK);

        ApiResponse<Balance> balanceResponse = new ApiResponse<>();
        balanceResponse.setStatus(ApiConstants.STATUS_OK);
        Balance balance = new Balance();
        balance.setAvailableBalance(new BigDecimal("1000.00"));
        balance.setBalance(new BigDecimal("1000.00"));
        balance.setCurrency("EUR");
        balanceResponse.setPayload(balance);

        ApiResponse<TransactionsPayload> transactionsResponse = new ApiResponse<>();
        transactionsResponse.setStatus(ApiConstants.STATUS_OK);

        ApiResponse<MoneyTransferResponse> moneyTransferResponse = new ApiResponse<>();
        moneyTransferResponse.setStatus(ApiConstants.STATUS_OK);

        // imposto mock per i metodi del servizio
        when(fabrickService.getAccounts()).thenReturn(accountsResponse);
        when(fabrickService.getAccountBalance(anyString())).thenReturn(balanceResponse);
        when(fabrickService.getAccountTransactions(anyString(), anyString(), anyString()))
                .thenReturn(transactionsResponse);
        when(fabrickService.createMoneyTransfer(anyString(), any(MoneyTransferRequest.class)))
                .thenReturn(moneyTransferResponse);

        // imposto mock per le chiamate REST
        // Creazione delle ResponseEntity per ogni tipo di risposta
        ResponseEntity<ApiResponse<AccountsPayload>> accountsResponseEntity = new ResponseEntity<>(accountsResponse,
                HttpStatus.OK);
        ResponseEntity<ApiResponse<Balance>> balanceResponseEntity = new ResponseEntity<>(balanceResponse,
                HttpStatus.OK);
        ResponseEntity<ApiResponse<TransactionsPayload>> transactionsResponseEntity = new ResponseEntity<>(
                transactionsResponse, HttpStatus.OK);
        ResponseEntity<ApiResponse<MoneyTransferResponse>> moneyTransferResponseEntity = new ResponseEntity<>(
                moneyTransferResponse, HttpStatus.OK);

        // specifico tipo esplicito per evitare warning di tipo
        // "unchecked"
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn((ResponseEntity) accountsResponseEntity)
                .thenReturn((ResponseEntity) balanceResponseEntity)
                .thenReturn((ResponseEntity) transactionsResponseEntity);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn((ResponseEntity) moneyTransferResponseEntity);
    }

    /**
     * Test di integrazione che verifica l'endpoint per ottenere gli account.
     * 
     * Questo test simula una richiesta HTTP GET all'endpoint dell'account
     * e verifica che:
     * - La risposta abbia status 200 OK
     * - Il campo "status" nella risposta JSON sia "OK"
     */
    @Test
    void getAccounts_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(get(ApiConstants.API_ACCOUNTS_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiConstants.STATUS_OK));
    }

    /**
     * Test di integrazione che verifica l'endpoint per ottenere il saldo di un
     * account.
     * 
     * Questo test simula una richiesta HTTP GET all'endpoint del saldo
     * e verifica che:
     * - La risposta abbia status 200 OK
     * - Il campo "status" nella risposta JSON sia "OK"
     * - La valuta nel payload sia "EUR"
     */
    @Test
    void getAccountBalance_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(
                get(ApiConstants.API_ACCOUNTS_BASE_PATH + "/{accountId}" + ApiConstants.BALANCE_ENDPOINT, accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiConstants.STATUS_OK))
                .andExpect(jsonPath("$.payload.currency").value("EUR"))
                .andExpect(jsonPath("$.payload.availableBalance").value(1000.00));
    }

    /**
     * Test di integrazione che verifica l'endpoint per ottenere le transazioni di
     * un account.
     * 
     * Questo test simula una richiesta HTTP GET all'endpoint delle transazioni
     * con parametri di query per l'intervallo di date e verifica che:
     * - La risposta abbia status 200 OK
     * - Il campo "status" nella risposta JSON sia "OK"
     */
    @Test
    void getAccountTransactions_shouldReturnOkStatus() throws Exception {
        String fromDate = "2023-01-01";
        String toDate = "2023-01-31";

        mockMvc.perform(get(ApiConstants.API_ACCOUNTS_BASE_PATH + "/{accountId}" + ApiConstants.TRANSACTIONS_ENDPOINT,
                accountId)
                .param("fromAccountingDate", fromDate)
                .param("toAccountingDate", toDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiConstants.STATUS_OK));
    }

    /**
     * Test di integrazione che verifica l'endpoint per creare un bonifico.
     * 
     * Questo test simula una richiesta HTTP POST all'endpoint dei bonifici
     * con un payload JSON valido e verifica che:
     * - La risposta abbia status 200 OK
     * - Il campo "status" nella risposta JSON sia "OK"
     */
    @Test
    void createMoneyTransfer_shouldReturnOkStatus() throws Exception {
        MoneyTransferRequest request = createSampleMoneyTransferRequest();

        mockMvc.perform(
                post(ApiConstants.API_ACCOUNTS_BASE_PATH + "/{accountId}" + ApiConstants.MONEY_TRANSFERS_ENDPOINT,
                        accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiConstants.STATUS_OK));
    }

    /**
     * Test di integrazione che verifica la gestione di richieste di bonifico non
     * valide.
     * 
     * Questo test simula una richiesta HTTP POST all'endpoint dei bonifici
     * con un payload JSON incompleto (mancano campi obbligatori) e verifica che:
     * - La risposta abbia status 400 BAD REQUEST
     */
    @Test
    void createMoneyTransfer_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        MoneyTransferRequest request = new MoneyTransferRequest(); // Invalid request missing required fields

        mockMvc.perform(
                post(ApiConstants.API_ACCOUNTS_BASE_PATH + "/{accountId}" + ApiConstants.MONEY_TRANSFERS_ENDPOINT,
                        accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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
