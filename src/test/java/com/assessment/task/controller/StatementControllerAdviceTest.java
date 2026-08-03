    package com.assessment.task.controller;

    import com.assessment.task.model.MonthlyBalance;
    import com.assessment.task.service.impl.MockOutboundApiClient;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.web.server.LocalServerPort;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.boot.resttestclient.TestRestTemplate;
    import java.math.BigDecimal;
    import static org.assertj.core.api.Assertions.assertThat;

    @AutoConfigureTestRestTemplate
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=mock")
    class StatementControllerAdviceTest {
        @Autowired
        private TestRestTemplate restTemplate;
        @Autowired
        private MockOutboundApiClient mockOutboundApiClient;

        @LocalServerPort
        String port;

        @Test
        void invalidMonthProducesBadRequest() {
            String payload = "{ \"accountId\": \"acct-1\", \"month\": \"not-a-month\" }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> resp = restTemplate.postForEntity("http://localhost:" + port + "/api/statements/process", entity, String.class);
            assertThat(resp.getStatusCode().value()).isEqualTo(400);
            assertThat(resp.getBody()).contains("Invalid month format. Expected YYYY-MM");
        }

        @Test
        void missingMonthProducesBadRequest() {
            String payload = "{ \"accountId\": \"acct-1\" }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> resp = restTemplate.postForEntity("http://localhost:" + port + "/api/statements/process", entity, String.class);
            assertThat(resp.getStatusCode().value()).isEqualTo(400);
            assertThat(resp.getBody()).contains("Month is required in YYYY-MM format");
        }

        @Test
        void validRequestFetchesCalculatesAndSendsToOutbound() {
            String payload = "{ \"accountId\": \"acct-1\", \"month\": \"2026-07\" }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<MonthlyBalance> resp = restTemplate.postForEntity(
                    "http://localhost:" + port + "/api/statements/process", entity, MonthlyBalance.class);

            // 1. Controller returned 200 with the computed balance (fetch + calculate happened)
            assertThat(resp.getStatusCode().value()).isEqualTo(200);
            MonthlyBalance body = resp.getBody();
            assertThat(body).isNotNull();
            assertThat(body.totalIncome()).isEqualByComparingTo(new BigDecimal("2650.00"));
            assertThat(body.totalSpending()).isEqualByComparingTo(new BigDecimal("725.60"));
            assertThat(body.balance()).isEqualByComparingTo(new BigDecimal("1924.40"));
            assertThat(body.transactions()).hasSize(5);

            // 2. The same balance send to the outbound client (send step happened when we made the request)
            MonthlyBalance sent = mockOutboundApiClient.getLastSent();
            assertThat(sent).isNotNull();
            assertThat(sent.balance()).isEqualByComparingTo(body.balance());
        }


    }
