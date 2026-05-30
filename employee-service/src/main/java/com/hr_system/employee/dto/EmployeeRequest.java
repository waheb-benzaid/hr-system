package com.hr_system.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating or updating an employee")
public class EmployeeRequest {

    @Schema(description = "Employee's first name", example = "John", minLength = 2, maxLength = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Employee's last name", example = "Doe", minLength = 2, maxLength = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Schema(description = "Employee's unique email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Schema(description = "Department the employee belongs to", example = "Engineering", minLength = 2, maxLength = 100)
    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    private String department;
}
