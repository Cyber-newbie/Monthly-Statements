package com.assessment.task.service.impl;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.model.Transaction;
import com.assessment.task.service.BalanceCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class DefaultBalanceCalculator implements BalanceCalculator {

    @Override
    public MonthlyBalance calculate(YearMonth month, List<Transaction> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalSpending = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            BigDecimal amt = t.amount();
            if (amt.signum() >= 0) {
                totalIncome = totalIncome.add(amt);
            } else {
                totalSpending = totalSpending.add(amt.abs());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalSpending);
        return new MonthlyBalance(month, totalIncome, totalSpending, balance, transactions);
    }
}
