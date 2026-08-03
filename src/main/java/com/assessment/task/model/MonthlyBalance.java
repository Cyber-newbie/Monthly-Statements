package com.assessment.task.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Result of calculating a monthly balance.
 */
@Schema(description = "Computed monthly balance summary")
public record MonthlyBalance(
        @Schema(description = "Year and month of the balance (YYYY-MM)") YearMonth month,
        @Schema(description = "Total income for the month") BigDecimal totalIncome,
        @Schema(description = "Total spending for the month") BigDecimal totalSpending,
        @Schema(description = "Income minus spending") BigDecimal balance,
        @Schema(description = "List of transactions used to compute the balance") List<Transaction> transactions
) {
}
