package com.assessment.task.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Immutable transaction record used across the application.
 */
@Schema(description = "A single bank transaction")
public record Transaction(
        @Schema(description = "Transaction date (YYYY-MM-DD)") LocalDate date,
        @Schema(description = "Amount: positive for credit, negative for debit") BigDecimal amount,
        @Schema(description = "Description / reference") String description,
        @Schema(description = "Type, e.g., CREDIT or DEBIT") String type
) {
}
