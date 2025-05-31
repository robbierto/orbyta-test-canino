package com.orbyta.banking.model.balance;

import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private String date;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
}
