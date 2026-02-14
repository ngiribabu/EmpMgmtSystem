package com.empmgmt.repository;

import com.empmgmt.model.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DependentRepository extends JpaRepository<Dependent, Integer> {
    List<Dependent> findByEmpId(Integer empId);
}
