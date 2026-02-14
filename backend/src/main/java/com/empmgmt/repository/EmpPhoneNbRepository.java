package com.empmgmt.repository;

import com.empmgmt.model.EmpPhoneNb;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpPhoneNbRepository extends JpaRepository<EmpPhoneNb, Integer> {
    List<EmpPhoneNb> findByEmpId(Integer empId);
}
