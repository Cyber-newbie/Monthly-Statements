package com.assessment.task.service.impl;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultBalanceCalculatorTest {

    private final DefaultBalanceCalculator calculator = new DefaultBalanceCalculator();

    @Test
    void calculatesIncomeAndSpendingCorrectly() {
        YearMonth month = YearMonth.of(2026, 7);
        List<Transaction> transactions = MockBankApiClient.sampleTransactions(month);

        MonthlyBalance result = calculator.calculate(month, transactions);

        assertEquals(new BigDecimal("2650.00"), result.totalIncome());
        assertEquals(new BigDecimal("725.60"), result.totalSpending());
        assertEquals(new BigDecimal("1924.40"), result.balance());
        assertEquals(5, result.transactions().size());
    }
}
