package com.assessment.task.service;

import com.assessment.task.model.Transaction;

import java.util.List;

/**
 * Convert raw statement payloads into domain transactions. Kept as an interface for
 * separation of concerns and easier testing.
 */
public interface StatementParser {
    List<Transaction> parse(String rawPayload) throws Exception;
}
