package com.assessment.task.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultStatementParserNegativeTest {

    private final DefaultStatementParser parser = new DefaultStatementParser();

    @Test
    void malformedJsonThrows() {
        String badJson = "{ this is not valid json ]";
        assertThrows(Exception.class, () -> parser.parse(badJson));
    }
}
