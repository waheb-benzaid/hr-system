package com.hr_system.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee data returned from the API")
public class EmployeeResponse {

    @Schema(description = "Unique identifier of the employee (UUID)", example = "749e5e78-767b-42f2-a61b-2209345f3a25")
    private UUID id;

    @Schema(description = "Employee's first name", example = "John")
    private String firstName;

    @Schema(description = "Employee's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Employee's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Department the employee belongs to", example = "Engineering")
    private String department;

    @Schema(description = "Whether the employee is currently active. false means soft-deleted.", example = "true")
    private boolean active;

    @Schema(description = "Timestamp when the employee record was created", example = "2026-05-30T01:31:10.453279")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update to this record", example = "2026-05-30T01:31:10.453312")
    private LocalDateTime updatedAt;

    @Schema(description = "Timestamp when the employee was deactivated. null if still active.", example = "null")
    private LocalDateTime deactivatedAt;
}
