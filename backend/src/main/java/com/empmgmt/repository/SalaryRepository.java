package com.empmgmt.repository;

import com.empmgmt.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    List<Salary> findByEmpIdOrderByEffDateDesc(Integer empId);
    Optional<Salary> findByEmpIdAndIsCurrent(Integer empId, String isCurrent);
}
