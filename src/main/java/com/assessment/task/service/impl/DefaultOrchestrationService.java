package com.assessment.task.service.impl;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.model.Transaction;
import com.assessment.task.service.BankApiClient;
import com.assessment.task.service.BalanceCalculator;
import com.assessment.task.service.OutboundApiClient;
import com.assessment.task.service.OrchestrationService;
import com.assessment.task.service.StatementParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class DefaultOrchestrationService implements OrchestrationService {

    private final BankApiClient bankApiClient;
    private final StatementParser parser;
    private final BalanceCalculator calculator;
    private final OutboundApiClient outboundApiClient;

    @Value("${task.default-account:default-account}")
    private String defaultAccount;

    public DefaultOrchestrationService(BankApiClient bankApiClient, StatementParser parser, BalanceCalculator calculator, OutboundApiClient outboundApiClient) {
        this.bankApiClient = bankApiClient;
        this.parser = parser;
        this.calculator = calculator;
        this.outboundApiClient = outboundApiClient;
    }

    @Override
    public MonthlyBalance process(String accountId, YearMonth month) throws Exception {
        String acct = (accountId == null || accountId.isBlank()) ? defaultAccount : accountId;
        List<Transaction> transactions = bankApiClient.fetchStatement(acct, month);
        MonthlyBalance balance = calculator.calculate(month, transactions);
        outboundApiClient.sendMonthlyBalance(balance);
        return balance;
    }

    @Override
    @Scheduled(cron = "0 0 3 1 * ?")
    public void scheduled() throws Exception {
        // example: run for the default account and last month
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        process(defaultAccount, lastMonth);
    }
}
