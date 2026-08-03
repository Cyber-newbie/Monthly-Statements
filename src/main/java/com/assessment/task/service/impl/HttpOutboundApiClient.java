package com.assessment.task.service.impl;

import com.assessment.task.error.ExternalServiceException;
import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.service.OutboundApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Primary
@Profile("!mock")
public class HttpOutboundApiClient implements OutboundApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String outboundUrl;

    public HttpOutboundApiClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${task.outbound-api.url}") String outboundUrl) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.outboundUrl = outboundUrl;
    }

    @Override
    public void sendMonthlyBalance(MonthlyBalance balance) throws Exception {
        String payload = mapper.writeValueAsString(balance);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(outboundUrl, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Failed to send monthly balance: " + resp.getStatusCode());
            }
        } catch (ExternalServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExternalServiceException("Error sending monthly balance to outbound API", ex);
        }
    }
}
