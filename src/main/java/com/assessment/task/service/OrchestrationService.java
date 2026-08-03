package com.assessment.task.service;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.model.Transaction;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.YearMonth;
import java.util.List;

public interface OrchestrationService {
    MonthlyBalance process(String accountId, YearMonth month) throws Exception;

    @Scheduled(cron = "0 0 2 1 * ?") // default: 02:00am on the 1st of every month
    default void scheduled() throws Exception {
        // Default no-op. Implementations can either override to call process on configured accounts,
        // or rely on the controller to trigger runs.
    }
}
