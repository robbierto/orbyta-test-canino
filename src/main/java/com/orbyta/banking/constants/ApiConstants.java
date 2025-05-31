package com.orbyta.banking.constants;

public final class ApiConstants {

    private ApiConstants() {
        
    }

    // API base paths
    public static final String API_ACCOUNTS_BASE_PATH = "/api/accounts";

    // API endpoints
    public static final String BALANCE_ENDPOINT = "/balance";
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    public static final String MONEY_TRANSFERS_ENDPOINT = "/payments/money-transfers";

    // Status codes
    public static final String STATUS_OK = "OK";
    public static final String STATUS_KO = "KO";

    // Date formats
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    // Money transfer constants
    public static final String TIMEZONE_EUROPE_ROME = "Europe/Rome";
}
