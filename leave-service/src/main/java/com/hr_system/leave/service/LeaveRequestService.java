package com.hr_system.leave.service;

import com.hr_system.leave.client.EmployeeClient;
import com.hr_system.leave.dto.LeaveRequestCreateDTO;
import com.hr_system.leave.dto.LeaveRequestResponse;
import com.hr_system.leave.dto.LeaveStatusUpdateDTO;
import com.hr_system.leave.exception.InvalidLeaveRequestException;
import com.hr_system.leave.exception.ResourceNotFoundException;
import com.hr_system.leave.mapper.LeaveRequestMapper;
import com.hr_system.leave.model.LeaveRequest;
import com.hr_system.leave.model.LeaveStatus;
import com.hr_system.leave.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmployeeClient employeeClient;

    @Transactional
    public LeaveRequestResponse createLeaveRequest(LeaveRequestCreateDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidLeaveRequestException("End date must be after or equal to start date");
        }

        boolean exists = employeeClient.employeeExists(dto.getEmployeeId());
        if (!exists) {
            throw new InvalidLeaveRequestException(
                    "Employee not found with id: " + dto.getEmployeeId());
        }

        if (leaveRequestRepository.existsByEmployeeIdAndStartDateAndEndDate(
                dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
            throw new InvalidLeaveRequestException(
                    "A leave request for employee " + dto.getEmployeeId() +
                    " from " + dto.getStartDate() + " to " + dto.getEndDate() + " already exists");
        }

        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(dto);
        LeaveRequest saved = leaveRequestRepository.saveAndFlush(leaveRequest);
        return leaveRequestMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LeaveRequestResponse getLeaveRequestById(UUID id) {
        LeaveRequest leaveRequest = findLeaveRequestOrThrow(id);
        return leaveRequestMapper.toResponse(leaveRequest);
    }

    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> getLeaveRequestsByEmployee(UUID employeeId, Pageable pageable) {
        return leaveRequestRepository.findAllByEmployeeId(employeeId, pageable)
                .map(leaveRequestMapper::toResponse);
    }

    @Transactional
    public LeaveRequestResponse updateLeaveStatus(UUID id, LeaveStatusUpdateDTO dto) {
        LeaveRequest leaveRequest = findLeaveRequestOrThrow(id);

        validateStatusTransition(leaveRequest.getStatus(), dto.getStatus());

        leaveRequest.setStatus(dto.getStatus());
        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);
        return leaveRequestMapper.toResponse(updated);
    }

    private void validateStatusTransition(LeaveStatus currentStatus, LeaveStatus newStatus) {
        if (currentStatus != LeaveStatus.PENDING) {
            throw new InvalidLeaveRequestException(
                    "Cannot change status from " + currentStatus + ". Only PENDING leave requests can be updated.");
        }

        if (newStatus == LeaveStatus.PENDING) {
            throw new InvalidLeaveRequestException(
                    "Cannot set status to PENDING. Use APPROVED or REJECTED.");
        }
    }

    private LeaveRequest findLeaveRequestOrThrow(UUID id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave request not found with id: " + id));
    }
}
