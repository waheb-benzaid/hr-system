package com.hr_system.leave.repository;

import com.hr_system.leave.model.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    Page<LeaveRequest> findAllByEmployeeId(UUID employeeId, Pageable pageable);

    boolean existsByEmployeeIdAndStartDateAndEndDate(UUID employeeId, LocalDate startDate, LocalDate endDate);
}
