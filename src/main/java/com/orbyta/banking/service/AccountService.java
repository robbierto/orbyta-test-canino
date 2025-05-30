package com.orbyta.banking.service;

import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.AccountsPayload;
import com.orbyta.banking.model.Balance;
import com.orbyta.banking.model.TransactionsPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AccountService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public AccountService(RestTemplate restTemplate,
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

        ResponseEntity<ApiResponse<AccountsPayload>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<AccountsPayload>>() {
                });

        return response.getBody();
    }

    public ApiResponse<Balance> getAccountBalance(String accountId) {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        String balanceUrl = apiUrl + "/" + accountId + "/balance";

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

        String transactionsUrl = apiUrl + "/" + accountId + "/transactions";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(transactionsUrl)
                .queryParam("fromAccountingDate", fromAccountingDate)
                .queryParam("toAccountingDate", toAccountingDate);

        ResponseEntity<ApiResponse<TransactionsPayload>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<TransactionsPayload>>() {
                });

        return response.getBody();
    }
}
