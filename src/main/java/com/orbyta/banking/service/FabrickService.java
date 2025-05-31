package com.orbyta.banking.service;

import com.orbyta.banking.constants.ApiConstants;
import com.orbyta.banking.constants.HeaderConstants;
import com.orbyta.banking.model.ApiResponse;
import com.orbyta.banking.model.account.AccountsPayload;
import com.orbyta.banking.model.balance.Balance;
import com.orbyta.banking.model.moneytransfer.MoneyTransferRequest;
import com.orbyta.banking.model.moneytransfer.MoneyTransferResponse;
import com.orbyta.banking.model.transaction.TransactionsPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        private static final Logger logger = LoggerFactory.getLogger(FabrickService.class);

        private final RestTemplate restTemplate;
        private final String apiUrl;
        private final String apiKey;

        public FabrickService(RestTemplate restTemplate,
                        @Value("${api.banking.url}") String apiUrl,
                        @Value("${api.banking.key}") String apiKey) {
                this.restTemplate = restTemplate;
                this.apiUrl = apiUrl;
                this.apiKey = apiKey;
                logger.info("FabrickService initialized with API URL: {}", apiUrl);
        }

        private HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HeaderConstants.AUTH_SCHEMA, HeaderConstants.AUTH_SCHEMA_VALUE);
                headers.set(HeaderConstants.API_KEY, apiKey);
                return headers;
        }

        public ApiResponse<AccountsPayload> getAccounts() {
                logger.debug("Fetching account info from external API");
                HttpEntity<?> entity = new HttpEntity<>(getHeaders());

                String url = UriComponentsBuilder.fromUriString(apiUrl)
                                .toUriString();

                logger.debug("Calling GET {}", url);
                ResponseEntity<ApiResponse<AccountsPayload>> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<AccountsPayload>>() {
                                });

                logger.info("Account info API response status: {}", response.getStatusCode());
                return response.getBody();
        }

        public ApiResponse<Balance> getAccountBalance(String accountId) {
                logger.debug("Fetching balance for account: {}", accountId);
                HttpEntity<?> entity = new HttpEntity<>(getHeaders());

                String balanceUrl = buildAccountUrl(accountId, ApiConstants.BALANCE_ENDPOINT)
                                .toUriString();

                logger.debug("Calling GET {}", balanceUrl);
                ResponseEntity<ApiResponse<Balance>> response = restTemplate.exchange(
                                balanceUrl,
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<Balance>>() {
                                });

                logger.info("Balance API response status: {} for account: {}", response.getStatusCode(), accountId);
                return response.getBody();
        }

        public ApiResponse<TransactionsPayload> getAccountTransactions(String accountId, String fromAccountingDate,
                        String toAccountingDate) {
                logger.debug("Fetching transactions for account: {} from: {} to: {}", accountId, fromAccountingDate,
                                toAccountingDate);
                HttpEntity<?> entity = new HttpEntity<>(getHeaders());

                String transactionsUrl = buildAccountUrl(accountId, ApiConstants.TRANSACTIONS_ENDPOINT)
                                .queryParam("fromAccountingDate", fromAccountingDate)
                                .queryParam("toAccountingDate", toAccountingDate)
                                .toUriString();

                logger.debug("Calling GET {}", transactionsUrl);
                ResponseEntity<ApiResponse<TransactionsPayload>> response = restTemplate.exchange(
                                transactionsUrl,
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<TransactionsPayload>>() {
                                });

                logger.info("Transactions API response status: {} for account: {}", response.getStatusCode(),
                                accountId);
                return response.getBody();
        }

        public ApiResponse<MoneyTransferResponse> createMoneyTransfer(String accountId, MoneyTransferRequest request) {
                logger.debug("Creating money transfer for account: {} with amount: {} {}",
                                accountId, request.getAmount(), request.getCurrency());

                HttpHeaders headers = getHeaders();
                headers.set(HeaderConstants.X_TIME_ZONE, ApiConstants.TIMEZONE_EUROPE_ROME);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<MoneyTransferRequest> entity = new HttpEntity<>(request, headers);

                String moneyTransferUrl = buildAccountUrl(accountId, ApiConstants.MONEY_TRANSFERS_ENDPOINT)
                                .toUriString();

                logger.debug("Calling POST {}", moneyTransferUrl);
                ResponseEntity<ApiResponse<MoneyTransferResponse>> response = restTemplate.exchange(
                                moneyTransferUrl,
                                HttpMethod.POST,
                                entity,
                                new ParameterizedTypeReference<ApiResponse<MoneyTransferResponse>>() {
                                });

                logger.info("Money transfer API response status: {} for account: {}", response.getStatusCode(),
                                accountId);
                return response.getBody();
        }

        // Metodo per costruire l'URL per le operazioni sull'account
        private UriComponentsBuilder buildAccountUrl(String accountId, String path) {
                return UriComponentsBuilder.fromUriString(apiUrl)
                                .pathSegment(accountId)
                                .path(path);
        }
}
