package com.hr_system.leave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating a new leave request")
public class LeaveRequestCreateDTO {

    @Schema(description = "UUID of the employee requesting leave (must exist in the Employee Service)", example = "749e5e78-767b-42f2-a61b-2209345f3a25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @Schema(description = "First day of the leave period. Must be today or a future date. Format: YYYY-MM-DD", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must not be in the past")
    private LocalDate startDate;

    @Schema(description = "Last day of the leave period. Must be on or after startDate. Format: YYYY-MM-DD", example = "2026-07-05", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must not be in the past")
    private LocalDate endDate;
}
