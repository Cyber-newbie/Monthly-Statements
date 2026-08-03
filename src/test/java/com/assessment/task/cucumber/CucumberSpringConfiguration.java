package com.assessment.task.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import io.cucumber.spring.CucumberContextConfiguration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class CucumberSpringConfiguration {

    // Shared WireMock servers for Cucumber scenarios
    public static final WireMockServer BANK_API = new WireMockServer(options().dynamicPort());
    public static final WireMockServer OUTBOUND_API = new WireMockServer(options().dynamicPort());

    static {
        BANK_API.start();
        OUTBOUND_API.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("task.bank-api.url", () -> "http://localhost:" + BANK_API.port());
        registry.add("task.outbound-api.url", () -> "http://localhost:" + OUTBOUND_API.port() + "/monthly-balances");
    }
}
