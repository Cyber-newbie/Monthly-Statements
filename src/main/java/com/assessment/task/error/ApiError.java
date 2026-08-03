package com.assessment.task.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API error response")
public record ApiError(
        @Schema(description = "Error type") ErrorType type,
        @Schema(description = "Human-readable message") String message,
        @Schema(description = "Optional details") String details
) {}
