package com.empmgmt.repository;

import com.empmgmt.model.EmpHist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpHistRepository extends JpaRepository<EmpHist, Integer> {
    List<EmpHist> findByEmpIdOrderByEffDateDesc(Integer empId);
}
