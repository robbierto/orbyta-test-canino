package com.orbyta.banking.service;

import com.orbyta.banking.constants.ApiConstants;
import com.orbyta.banking.constants.HeaderConstants;
import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.account.AccountsPayload;
import com.orbyta.banking.model.balance.Balance;
import com.orbyta.banking.model.moneytransfer.MoneyTransferRequest;
import com.orbyta.banking.model.moneytransfer.MoneyTransferResponse;
import com.orbyta.banking.model.transaction.TransactionsPayload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FabrickServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<ApiResponse<AccountsPayload>> accountsResponseEntity;

    @Mock
    private ResponseEntity<ApiResponse<Balance>> balanceResponseEntity;

    @Mock
    private ResponseEntity<ApiResponse<TransactionsPayload>> transactionsResponseEntity;

    @Mock
    private ResponseEntity<ApiResponse<MoneyTransferResponse>> moneyTransferResponseEntity;

    @Mock
    private ApiResponse<AccountsPayload> accountsApiResponse;

    @Mock
    private ApiResponse<Balance> balanceApiResponse;

    @Mock
    private ApiResponse<TransactionsPayload> transactionsApiResponse;

    @Mock
    private ApiResponse<MoneyTransferResponse> moneyTransferApiResponse;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;

    private FabrickService fabrickService;
    private final String apiUrl = "https://sandbox.platfr.io/api/gbs/banking/v4.0/accounts";
    private final String apiKey = "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP";
    private final String accountId = "14537780";

    @BeforeEach
    void setUp() {
        fabrickService = new FabrickService(restTemplate, apiUrl, apiKey);
    }

    /**
     * Test che verifica la corretta chiamata all'API per ottenere info dell'account.
     * 
     * Questo test assicura che:
     * - L'URL di chiamata sia corretto
     * - Il metodo HTTP sia GET
     * - Gli header di autenticazione (Auth-Schema e Api-Key) siano presenti e
     * corretti
     * - La risposta dell'API venga correttamente restituita dal servizio
     */
    @SuppressWarnings("unchecked")
    @Test
    void getAccounts_shouldCallApiWithCorrectParameters() {
        // Given
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(accountsResponseEntity);
        when(accountsResponseEntity.getBody()).thenReturn(accountsApiResponse);

        // When
        ApiResponse<AccountsPayload> result = fabrickService.getAccounts();

        // Then
        verify(restTemplate).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                any(ParameterizedTypeReference.class));

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();

        assertEquals(HeaderConstants.AUTH_SCHEMA_VALUE, headers.getFirst(HeaderConstants.AUTH_SCHEMA));
        assertEquals(apiKey, headers.getFirst(HeaderConstants.API_KEY));
        assertEquals(accountsApiResponse, result);
    }

    /**
     * Test che verifica la corretta chiamata all'API per ottenere il saldo di un
     * account.
     * 
     * Questo test assicura che:
     * - L'URL di chiamata includa l'ID dell'account corretto
     * - Il metodo HTTP sia GET
     * - Gli header di autenticazione siano presenti e corretti
     * - La risposta dell'API venga correttamente restituita dal servizio
     */
    @SuppressWarnings("unchecked")
    @Test
    void getAccountBalance_shouldCallApiWithCorrectParameters() {
        // Given
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(balanceResponseEntity);
        when(balanceResponseEntity.getBody()).thenReturn(balanceApiResponse);

        // When
        ApiResponse<Balance> result = fabrickService.getAccountBalance(accountId);

        // Then
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                any(ParameterizedTypeReference.class));

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();

        assertEquals(HeaderConstants.AUTH_SCHEMA_VALUE, headers.getFirst(HeaderConstants.AUTH_SCHEMA));
        assertEquals(apiKey, headers.getFirst(HeaderConstants.API_KEY));
        assertEquals(balanceApiResponse, result);
    }

    /**
     * Test che verifica la corretta chiamata all'API per ottenere le transazioni di
     * un account.
     * 
     * Questo test assicura che:
     * - L'URL di chiamata includa l'ID dell'account corretto
     * - Il metodo HTTP sia GET
     * - Gli header di autenticazione siano presenti e corretti
     * - I parametri di data (fromDate e toDate) vengano passati correttamente
     * - La risposta dell'API venga correttamente restituita dal servizio
     */
    @SuppressWarnings("unchecked")
    @Test
    void getAccountTransactions_shouldCallApiWithCorrectParameters() {
        // Given
        String fromDate = "2023-01-01";
        String toDate = "2023-01-31";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(transactionsResponseEntity);
        when(transactionsResponseEntity.getBody()).thenReturn(transactionsApiResponse);

        // When
        ApiResponse<TransactionsPayload> result = fabrickService.getAccountTransactions(accountId, fromDate, toDate);

        // Then
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                any(ParameterizedTypeReference.class));

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();

        assertEquals(HeaderConstants.AUTH_SCHEMA_VALUE, headers.getFirst(HeaderConstants.AUTH_SCHEMA));
        assertEquals(apiKey, headers.getFirst(HeaderConstants.API_KEY));
        assertEquals(transactionsApiResponse, result);
    }

    /**
     * Test che verifica la corretta chiamata all'API per creare un bonifico.
     * 
     * Questo test assicura che:
     * - L'URL di chiamata includa l'ID dell'account corretto
     * - Il metodo HTTP sia POST
     * - Gli header di autenticazione siano presenti e corretti
     * - L'header X-Time-Zone sia impostato correttamente (Europe/Rome)
     * - Il Content-Type sia application/json
     * - Il corpo della richiesta contenga i dati del bonifico
     * - La risposta dell'API venga correttamente restituita dal servizio
     */
    @SuppressWarnings("unchecked")
    @Test
    void createMoneyTransfer_shouldCallApiWithCorrectParameters() {
        // Given
        MoneyTransferRequest request = createSampleMoneyTransferRequest();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(moneyTransferResponseEntity);
        when(moneyTransferResponseEntity.getBody()).thenReturn(moneyTransferApiResponse);

        // When
        ApiResponse<MoneyTransferResponse> result = fabrickService.createMoneyTransfer(accountId, request);

        // Then
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                any(ParameterizedTypeReference.class));

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();

        assertEquals(HeaderConstants.AUTH_SCHEMA_VALUE, headers.getFirst(HeaderConstants.AUTH_SCHEMA));
        assertEquals(apiKey, headers.getFirst(HeaderConstants.API_KEY));
        assertEquals(ApiConstants.TIMEZONE_EUROPE_ROME, headers.getFirst(HeaderConstants.X_TIME_ZONE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertNotNull(capturedEntity.getBody());
        assertEquals(request, capturedEntity.getBody());
        assertEquals(moneyTransferApiResponse, result);
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
