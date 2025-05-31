package com.orbyta.banking.model.moneytransfer;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferResponse {
    private String moneyTransferId;
    private String status;
    private String direction;
    private Creditor creditor;
    private Debtor debtor;
    private String cro;
    private String uri;
    private String trn;
    private String description;
    private String createdDatetime;
    private String accountedDatetime;
    private String debtorValueDate;
    private String creditorValueDate;
    private Amount amount;
    private boolean isUrgent;
    private boolean isInstant;
    private String feeType;
    private String feeAccountId;
    private List<Fee> fees;
    private boolean hasTaxRelief;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creditor {
        private String name;
        private Account account;
        private Address address;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Account {
            private String accountCode;
            private String bicCode;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Address {
            private String address;
            private String city;
            private String countryCode;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Debtor {
        private String name;
        private Account account;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Account {
            private String accountCode;
            private String bicCode;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        private BigDecimal debtorAmount;
        private String debtorCurrency;
        private BigDecimal creditorAmount;
        private String creditorCurrency;
        private String creditorCurrencyDate;
        private BigDecimal exchangeRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fee {
        private String feeCode;
        private String description;
        private BigDecimal amount;
        private String currency;
    }
}
