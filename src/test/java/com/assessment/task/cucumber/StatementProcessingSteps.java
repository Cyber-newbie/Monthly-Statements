package com.assessment.task.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StatementProcessingSteps {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    private WireMockServer bankApi;
    private WireMockServer outboundApi;
    private ResponseEntity<String> response;
    private static final Logger log = LoggerFactory.getLogger(StatementProcessingSteps.class);
    // Reuse the WireMock servers started in CucumberSpringConfiguration
    public StatementProcessingSteps() {
        this.bankApi = CucumberSpringConfiguration.BANK_API;
        this.outboundApi = CucumberSpringConfiguration.OUTBOUND_API;
    }

    @Before
    public void resetWireMocks() {
        bankApi.resetAll();
        outboundApi.resetAll();
        response = null;
    }

    @Given("the bank API returns the following transactions for account {string} and month {string}:")
    public void bankApiReturnsTransactions(String accountId, String month, DataTable table) {
        List<Map<String, String>> rows = table.asMaps();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            if (i > 0) json.append(",");
            json.append(String.format(
                    "{\"date\":\"%s\",\"amount\":%s,\"description\":\"%s\",\"type\":\"%s\"}",
                    row.get("date"), row.get("amount"), row.get("description"), row.get("type")));
        }
        json.append("]");

        bankApi.stubFor(get(urlEqualTo("/accounts/" + accountId + "/statements/" + month))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json.toString())));
    }

    @Given("the bank API is down for account {string} and month {string}")
    public void bankApiIsDown(String accountId, String month) {
        bankApi.stubFor(get(urlEqualTo("/accounts/" + accountId + "/statements/" + month))
                .willReturn(aResponse().withStatus(500)));
    }

    @And("the outbound API is ready to accept a monthly balance")
    public void outboundApiAcceptsBalance() {
        outboundApi.stubFor(post(urlEqualTo("/monthly-balances"))
                .willReturn(aResponse().withStatus(200)));
    }

    @When("I request statement processing for account {string} and month {string}")
    public void requestStatementProcessing(String accountId, String month) {
        String payload = String.format("{\"accountId\":\"%s\",\"month\":\"%s\"}", accountId, month);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity("http://localhost:" + port + "/api/statements/process", entity, String.class);
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        log.info("Response status: {}", response.getStatusCode());
        log.info("Response body: {}", response.getBody());
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @Then("the computed balance should be:")
    public void computedBalanceShouldBe(DataTable table) {
        Map<String, String> expected = table.asMap(String.class, String.class);
        assertThat(response.getBody()).contains("\"totalIncome\":" + expected.get("totalIncome"));
        assertThat(response.getBody()).contains("\"totalSpending\":" + expected.get("totalSpending"));
        assertThat(response.getBody()).contains("\"balance\":" + expected.get("balance"));
    }

    @Then("the outbound API should have received a balance of {double}")
    public void outboundApiShouldHaveReceived(Double expectedBalance) {
        outboundApi.verify(postRequestedFor(urlEqualTo("/monthly-balances"))
                .withRequestBody(containing("\"balance\":" + expectedBalance.toString())));
    }
}