package com.assessment.task.service.impl;

import com.assessment.task.model.Transaction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultStatementParserTest {

    private final DefaultStatementParser parser = new DefaultStatementParser();

    @Test
    void parsesJsonArrayToTransactions() throws Exception {
        String json = "[\n" +
                "  { \"date\": \"2026-07-01\", \"amount\": 2500.00, \"description\": \"Salary\", \"type\": \"CREDIT\" },\n" +
                "  { \"date\": \"2026-07-05\", \"amount\": -45.60, \"description\": \"Groceries\", \"type\": \"DEBIT\" }\n" +
                "]";

        List<Transaction> txs = parser.parse(json);
        assertEquals(2, txs.size());
        assertEquals("Salary", txs.get(0).description());
        assertEquals("CREDIT", txs.get(0).type());
    }
}
