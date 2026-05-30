package com.hr_system.leave.controller;

import com.hr_system.leave.dto.LeaveRequestCreateDTO;
import com.hr_system.leave.dto.LeaveRequestResponse;
import com.hr_system.leave.dto.LeaveStatusUpdateDTO;
import com.hr_system.leave.exception.ErrorResponse;
import com.hr_system.leave.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Requests", description = "Operations for managing employee leave requests — create, retrieve and update status (PENDING → APPROVED / REJECTED)")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @Operation(
            summary = "Create a leave request",
            description = "Creates a new leave request for a given employee. " +
                    "Validates the employee by calling the Employee Service synchronously. " +
                    "Rules: start date must not be in the past, end date must be ≥ start date, " +
                    "and no duplicate requests with the same employee + dates are allowed. " +
                    "Returns 503 if the Employee Service is unreachable."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Leave request created with status PENDING",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LeaveRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed, invalid dates, unknown employee, or duplicate request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Employee Service is unavailable — cannot validate employee",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<LeaveRequestResponse> createLeaveRequest(
            @Valid @RequestBody LeaveRequestCreateDTO dto) {
        LeaveRequestResponse response = leaveRequestService.createLeaveRequest(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get a leave request by ID",
            description = "Retrieves a single leave request by its UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave request found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LeaveRequestResponse.class))),
            @ApiResponse(responseCode = "404", description = "Leave request not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestResponse> getLeaveRequestById(
            @Parameter(description = "UUID of the leave request", required = true)
            @PathVariable UUID id) {
        LeaveRequestResponse response = leaveRequestService.getLeaveRequestById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get leave requests by employee",
            description = "Returns a paginated list of all leave requests for a given employee UUID. " +
                    "Supports pagination via `page`, `size`, and `sort` query parameters. " +
                    "No Employee Service call is made — the employee ID is stored locally."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of leave requests returned")
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<LeaveRequestResponse>> getLeaveRequestsByEmployee(
            @Parameter(description = "UUID of the employee whose leave requests to retrieve", required = true)
            @PathVariable UUID employeeId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<LeaveRequestResponse> leaves = leaveRequestService.getLeaveRequestsByEmployee(employeeId, pageable);
        return ResponseEntity.ok(leaves);
    }

    @Operation(
            summary = "Update leave request status",
            description = "Approves or rejects a leave request. " +
                    "Only PENDING requests can be updated. " +
                    "Valid transitions: PENDING → APPROVED, PENDING → REJECTED. " +
                    "Once APPROVED or REJECTED, the status cannot be changed again."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LeaveRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status transition or bad request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Leave request not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<LeaveRequestResponse> updateLeaveStatus(
            @Parameter(description = "UUID of the leave request to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody LeaveStatusUpdateDTO dto) {
        LeaveRequestResponse response = leaveRequestService.updateLeaveStatus(id, dto);
        return ResponseEntity.ok(response);
    }
}
