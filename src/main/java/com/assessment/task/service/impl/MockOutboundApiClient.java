package com.assessment.task.service.impl;

import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.service.OutboundApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Mock outbound client — logs the payload instead of performing HTTP calls. Active under 'mock' profile.
 * helps with testing and development without needing an actual outbound service.
 */
@Service
@Profile("mock")
public class MockOutboundApiClient implements OutboundApiClient {

    private static final Logger log = LoggerFactory.getLogger(MockOutboundApiClient.class);
    private final AtomicReference<MonthlyBalance> lastSent = new AtomicReference<>();

    @Override
    public void sendMonthlyBalance(MonthlyBalance balance) {
        log.info("Mock send monthly balance: {}", balance);
        lastSent.set(balance);
    }

    public MonthlyBalance getLastSent() {
        return lastSent.get();
    }
}
