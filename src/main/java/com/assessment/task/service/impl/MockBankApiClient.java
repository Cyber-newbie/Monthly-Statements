package com.assessment.task.service.impl;

import com.assessment.task.model.Transaction;
import com.assessment.task.service.BankApiClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Simple mock implementation that returns a handful of hard-coded transactions.
 * Active when the 'mock' Spring profile is enabled.
 */
@Service
@Profile("mock")
public class MockBankApiClient implements BankApiClient {

    @Override
    public List<Transaction> fetchStatement(String accountId, YearMonth month) {
        // Return a simple deterministic set of transactions for the requested month
        return sampleTransactions(month);
    }

    /**
     * Deterministic sample transactions for the requested month. Exposed as a static
     * factory so tests can reuse the exact same
     */
    public static List<Transaction> sampleTransactions(YearMonth month) {
        return List.of(
                new Transaction(month.atDay(1), new BigDecimal("2500.00"), "Salary", "CREDIT"),
                new Transaction(month.atDay(3), new BigDecimal("-45.60"), "Groceries", "DEBIT"),
                new Transaction(month.atDay(7), new BigDecimal("-120.00"), "Utilities", "DEBIT"),
                new Transaction(month.atDay(15), new BigDecimal("-560.00"), "Rent", "DEBIT"),
                new Transaction(month.atDay(20), new BigDecimal("150.00"), "Freelance", "CREDIT")
        );
    }
}
