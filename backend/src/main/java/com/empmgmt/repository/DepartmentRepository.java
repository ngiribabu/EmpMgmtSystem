package com.empmgmt.repository;

import com.empmgmt.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByIsActive(String isActive);
    List<Department> findByDeptNameContainingIgnoreCase(String name);
}
