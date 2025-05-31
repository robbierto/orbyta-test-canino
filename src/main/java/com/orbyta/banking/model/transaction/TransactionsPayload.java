package com.orbyta.banking.model.transaction;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsPayload {
    private List<Transaction> list;
}
