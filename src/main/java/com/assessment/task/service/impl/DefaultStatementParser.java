package com.assessment.task.service.impl;

import com.assessment.task.model.Transaction;
import com.assessment.task.service.StatementParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Basic JSON parser that maps a JSON array of transactions into Transaction records.
 * Expected payload format: [{"date":"2026-07-01","amount":2500.00,"description":"Salary","type":"CREDIT"}, ...]
 */
@Service
public class DefaultStatementParser implements StatementParser {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public List<Transaction> parse(String rawPayload) throws Exception {
        return mapper.readValue(rawPayload, new TypeReference<List<Transaction>>(){});
    }
}
