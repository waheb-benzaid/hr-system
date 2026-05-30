package com.hr_system.employee.controller;

import com.hr_system.employee.dto.EmployeeRequest;
import com.hr_system.employee.dto.EmployeeResponse;
import com.hr_system.employee.exception.ErrorResponse;
import com.hr_system.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Operations for managing employees — create, retrieve, update, activate and deactivate")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Create a new employee",
            description = "Creates a new employee record. The email must be unique across all employees."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — check field-level errors in response",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "An employee with this email already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get employee by ID",
            description = "Retrieves a single employee record by their UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @Parameter(description = "UUID of the employee", required = true, example = "749e5e78-767b-42f2-a61b-2209345f3a25")
            @PathVariable UUID id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List all employees",
            description = "Returns a paginated list of employees. Use the `active` parameter to filter by status. " +
                    "Supports pagination via `page`, `size`, and `sort` query parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of employees returned successfully")
    })
    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @Parameter(description = "Filter by active status. `true` = active only, `false` = inactive only. Omit to return all.")
            @RequestParam(required = false) Boolean active,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(active, pageable);
        return ResponseEntity.ok(employees);
    }

    @Operation(
            summary = "Update employee information",
            description = "Fully updates an employee's data. All fields are required. " +
                    "The email can be changed as long as it doesn't conflict with another employee's email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already used by another employee",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @Parameter(description = "UUID of the employee to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deactivate an employee",
            description = "Soft-deletes an employee by setting their `active` flag to `false` and recording the deactivation timestamp. " +
                    "The record is preserved for audit and historical purposes. Use the activate endpoint to reverse this."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee deactivated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<EmployeeResponse> deactivateEmployee(
            @Parameter(description = "UUID of the employee to deactivate", required = true)
            @PathVariable UUID id) {
        EmployeeResponse response = employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Activate an employee",
            description = "Reactivates a previously deactivated employee by setting their `active` flag back to `true` and clearing the deactivation timestamp."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee activated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<EmployeeResponse> activateEmployee(
            @Parameter(description = "UUID of the employee to activate", required = true)
            @PathVariable UUID id) {
        EmployeeResponse response = employeeService.activateEmployee(id);
        return ResponseEntity.ok(response);
    }
}
