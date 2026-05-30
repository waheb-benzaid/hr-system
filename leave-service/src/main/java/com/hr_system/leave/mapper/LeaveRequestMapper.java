package com.hr_system.leave.mapper;

import com.hr_system.leave.dto.LeaveRequestCreateDTO;
import com.hr_system.leave.dto.LeaveRequestResponse;
import com.hr_system.leave.model.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    public LeaveRequest toEntity(LeaveRequestCreateDTO dto) {
        return LeaveRequest.builder()
                .employeeId(dto.getEmployeeId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }

    public LeaveRequestResponse toResponse(LeaveRequest entity) {
        return LeaveRequestResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
