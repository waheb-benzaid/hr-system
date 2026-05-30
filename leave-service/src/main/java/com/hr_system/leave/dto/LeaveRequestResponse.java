package com.hr_system.leave.dto;

import com.hr_system.leave.model.LeaveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Leave request data returned from the API")
public class LeaveRequestResponse {

    @Schema(description = "Unique identifier of the leave request (UUID)", example = "e4548634-44b3-4846-9d4d-bbfb40a9d5f0")
    private UUID id;

    @Schema(description = "UUID of the employee who submitted this leave request", example = "749e5e78-767b-42f2-a61b-2209345f3a25")
    private UUID employeeId;

    @Schema(description = "First day of the leave period", example = "2026-07-01")
    private LocalDate startDate;

    @Schema(description = "Last day of the leave period", example = "2026-07-05")
    private LocalDate endDate;

    @Schema(description = "Current status of the leave request", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private LeaveStatus status;

    @Schema(description = "Timestamp when the leave request was submitted", example = "2026-05-30T01:31:20.053856")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last status update", example = "2026-05-30T01:31:20.053917")
    private LocalDateTime updatedAt;
}
