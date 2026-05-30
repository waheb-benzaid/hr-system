package com.hr_system.employee.service;

import com.hr_system.employee.dto.EmployeeRequest;
import com.hr_system.employee.dto.EmployeeResponse;
import com.hr_system.employee.exception.DuplicateResourceException;
import com.hr_system.employee.exception.ResourceNotFoundException;
import com.hr_system.employee.mapper.EmployeeMapper;
import com.hr_system.employee.model.Employee;
import com.hr_system.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new DuplicateResourceException(
                    "Employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID id) {
        Employee employee = findEmployeeOrThrow(id);
        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Boolean active, Pageable pageable) {
        Page<Employee> employees;

        if (active != null) {
            employees = employeeRepository.findAllByActive(active, pageable);
        } else {
            employees = employeeRepository.findAll(pageable);
        }

        return employees.map(employeeMapper::toResponse);
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);

        if (employeeRepository.existsByEmailAndIdNot(request.getEmail().toLowerCase().trim(), id)) {
            throw new DuplicateResourceException(
                    "Employee with email '" + request.getEmail() + "' already exists");
        }

        employeeMapper.updateEntity(employee, request);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public EmployeeResponse deactivateEmployee(UUID id) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setActive(false);
        employee.setDeactivatedAt(LocalDateTime.now());
        Employee deactivatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(deactivatedEmployee);
    }

    @Transactional
    public EmployeeResponse activateEmployee(UUID id) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setActive(true);
        employee.setDeactivatedAt(null);
        Employee activatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(activatedEmployee);
    }

    private Employee findEmployeeOrThrow(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));
    }
}
