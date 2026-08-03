package com.assessment.task.service.impl;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.service.OutboundApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Fallback outbound implementation used when no mock profile is active. It logs a warning and no-ops.
 */
@Service
@Profile("!mock")
public class FallbackOutboundApiClient implements OutboundApiClient {

    private static final Logger log = LoggerFactory.getLogger(FallbackOutboundApiClient.class);

    @Override
    public void sendMonthlyBalance(MonthlyBalance balance) {
        log.warn("No outbound API configured; not sending monthly balance. Enable 'mock' profile or add an HTTP OutboundApiClient.");
    }
}
