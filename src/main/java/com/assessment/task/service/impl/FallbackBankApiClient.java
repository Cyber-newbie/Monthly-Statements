package com.assessment.task.service.impl;

import com.assessment.task.error.ExternalServiceException;
import com.assessment.task.model.Transaction;
import com.assessment.task.service.BankApiClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

/**
 * Fallback bean active when the 'mock' profile is NOT active. It throws a helpful exception
 * so developers know to enable the mock profile or configure an external bank API URL.
 */
@Service
@Profile("!mock")
public class FallbackBankApiClient implements BankApiClient {

    @Override
    public List<Transaction> fetchStatement(String accountId, YearMonth month) {
        throw new ExternalServiceException("No BankApiClient implementation available. Enable 'mock' profile or provide a real implementation.");
    }
}
