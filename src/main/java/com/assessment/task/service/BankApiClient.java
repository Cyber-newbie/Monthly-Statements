package com.assessment.task.service;

import com.assessment.task.model.Transaction;

import java.time.YearMonth;
import java.util.List;

public interface BankApiClient {
    /**
     * Fetch statement transactions for the given account and month.
     */
    List<Transaction> fetchStatement(String accountId, YearMonth month) throws Exception;
}
