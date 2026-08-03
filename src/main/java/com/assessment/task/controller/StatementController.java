package com.assessment.task.controller;

import com.assessment.task.error.ApiError;
import com.assessment.task.error.InvalidRequestException;
import com.assessment.task.model.MonthlyBalance;
import com.assessment.task.service.OrchestrationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/statements")
@Tag(name = "Statements", description = "Endpoints to trigger statement processing")
public class StatementController {

    private final OrchestrationService orchestrationService;

    public StatementController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @Operation(summary = "Process monthly statement", description = "Fetches statements for an account and month, computes totals and sends the result to the outbound API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account and month to process",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"accountId\": \"acct-1\", \"month\": \"2026-07\" }")
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Computed monthly balance",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MonthlyBalance.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid or missing month",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "502", description = "External bank/outbound service failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/process")
    public ResponseEntity<MonthlyBalance> process(@Valid @RequestBody ProcessRequest request) throws Exception {

        YearMonth month;
        month = YearMonth.parse(request.month());

        MonthlyBalance result = orchestrationService.process(request.accountId(), month);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "Health check", description = "Simple liveness check confirming the service is up and responding")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service is up",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "StatementController is up and running")))
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("StatementController is up and running");
    }
    public record ProcessRequest(String accountId,
                                 @NotBlank(message = "Month is required in YYYY-MM format")
                                 @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "Invalid month format. Expected YYYY-MM")
                                 String month
    ) {}
}
