package com.assessment.task.service.impl;

import com.assessment.task.error.ExternalServiceException;
import com.assessment.task.model.Transaction;
import com.assessment.task.service.BankApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
import java.util.List;

@Service
@Primary
@Profile("!mock")
public class HttpBankApiClient implements BankApiClient {

    private final RestTemplate restTemplate;
    private final String bankApiBaseUrl;
    private final DefaultStatementParser parser;
    private final ObjectMapper mapper;
    public HttpBankApiClient(RestTemplate restTemplate, @Value("${task.bank-api.url}") String bankApiBaseUrl, DefaultStatementParser parser, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.bankApiBaseUrl = bankApiBaseUrl;
        this.parser = parser;
        this.mapper = mapper;
    }

    @Override
    public List<Transaction> fetchStatement(String accountId, YearMonth month) throws Exception {
        String monthStr = month.toString(); // e.g., 2026-07
        String url = String.format("%s/accounts/%s/statements/%s", bankApiBaseUrl, accountId, monthStr);
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new ExternalServiceException("Failed to fetch statement from bank API: " + resp.getStatusCode());
            }
            String body = resp.getBody();
            return parser.parse(body);
        } catch (ExternalServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExternalServiceException("Error fetching statement from bank API", ex);
        }
    }
}
