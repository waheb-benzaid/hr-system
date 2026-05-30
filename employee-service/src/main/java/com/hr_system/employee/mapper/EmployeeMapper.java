package com.hr_system.employee.mapper;

import com.hr_system.employee.dto.EmployeeRequest;
import com.hr_system.employee.dto.EmployeeResponse;
import com.hr_system.employee.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase().trim())
                .department(request.getDepartment())
                .build();
    }

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .active(employee.isActive())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .deactivatedAt(employee.getDeactivatedAt())
                .build();
    }

    public void updateEntity(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail().toLowerCase().trim());
        employee.setDepartment(request.getDepartment());
    }
}
