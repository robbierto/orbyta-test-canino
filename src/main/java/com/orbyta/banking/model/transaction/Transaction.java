package com.orbyta.banking.model.transaction;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String transactionId;
    private String operationId;
    private String accountingDate;
    private String valueDate;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private String description;
}
