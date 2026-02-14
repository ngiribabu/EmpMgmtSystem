package com.empmgmt.repository;

import com.empmgmt.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
    List<LeaveType> findByIsActive(String isActive);
}
