package com.orbyta.banking.service;

import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.account.AccountsPayload;
import com.orbyta.banking.model.balance.Balance;
import com.orbyta.banking.model.moneytransfer.MoneyTransferRequest;
import com.orbyta.banking.model.moneytransfer.MoneyTransferResponse;
import com.orbyta.banking.model.transaction.TransactionsPayload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FabrickService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public FabrickService(RestTemplate restTemplate,
            @Value("${api.banking.url}") String apiUrl,
            @Value("${api.banking.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Auth-Schema", "S2S");
        headers.set("apiKey", apiKey);
        return headers;
    }

    public ApiResponse<AccountsPayload> getAccounts() {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .toUriString();

        ResponseEntity<ApiResponse<AccountsPayload>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<AccountsPayload>>() {
                });

        return response.getBody();
    }

    public ApiResponse<Balance> getAccountBalance(String accountId) {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        String balanceUrl = buildAccountUrl(accountId, "/balance")
                .toUriString();

        ResponseEntity<ApiResponse<Balance>> response = restTemplate.exchange(
                balanceUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<Balance>>() {
                });

        return response.getBody();
    }

    public ApiResponse<TransactionsPayload> getAccountTransactions(String accountId, String fromAccountingDate,
            String toAccountingDate) {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        String transactionsUrl = buildAccountUrl(accountId, "/transactions")
                .queryParam("fromAccountingDate", fromAccountingDate)
                .queryParam("toAccountingDate", toAccountingDate)
                .toUriString();

        ResponseEntity<ApiResponse<TransactionsPayload>> response = restTemplate.exchange(
                transactionsUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<TransactionsPayload>>() {
                });

        return response.getBody();
    }

    public ApiResponse<MoneyTransferResponse> createMoneyTransfer(String accountId, MoneyTransferRequest request) {
        HttpHeaders headers = getHeaders();
        headers.set("X-Time-Zone", "Europe/Rome");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MoneyTransferRequest> entity = new HttpEntity<>(request, headers);

        String moneyTransferUrl = buildAccountUrl(accountId, "/payments/money-transfers")
                .toUriString();

        ResponseEntity<ApiResponse<MoneyTransferResponse>> response = restTemplate.exchange(
                moneyTransferUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<MoneyTransferResponse>>() {
                });

        return response.getBody();
    }

    // Metodo per costruire l'URL per le operazioni sull'account
    private UriComponentsBuilder buildAccountUrl(String accountId, String path) {
        return UriComponentsBuilder.fromUriString(apiUrl)
                .pathSegment(accountId)
                .path(path);
    }
}
