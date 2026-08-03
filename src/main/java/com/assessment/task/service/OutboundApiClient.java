package com.assessment.task.service;

import com.assessment.task.model.MonthlyBalance;

public interface OutboundApiClient {
    /**
     * Send the computed monthly balance to an external API.
     */
    void sendMonthlyBalance(MonthlyBalance balance) throws Exception;
}
