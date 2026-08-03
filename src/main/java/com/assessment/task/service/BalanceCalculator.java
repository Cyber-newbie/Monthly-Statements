package com.assessment.task.service;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.model.Transaction;

import java.time.YearMonth;
import java.util.List;

public interface BalanceCalculator {
    MonthlyBalance calculate(YearMonth month, List<Transaction> transactions);
}
