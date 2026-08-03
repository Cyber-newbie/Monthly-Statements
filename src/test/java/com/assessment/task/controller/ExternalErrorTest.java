package com.assessment.task.controller;

import com.assessment.task.error.ExternalServiceException;
import com.assessment.task.service.OrchestrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.resttestclient.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=mock")
class ExternalErrorTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    String port;
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public OrchestrationService failingOrchestration() {
            return (accountId, month) -> {
                throw new ExternalServiceException("bank down");
            };
        }
    }

    @Test
    void externalServiceErrorIsMapped() {
        String payload = "{ \"accountId\": \"acct-1\", \"month\": \"2026-07\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity("http://localhost:" + port + "/api/statements/process", entity, String.class);
        assertThat(resp.getStatusCode().value()).isEqualTo(502);
        assertThat(resp.getBody()).contains("EXTERNAL_SERVICE_ERROR");
    }
}