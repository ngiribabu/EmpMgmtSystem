package com.empmgmt.repository;

import com.empmgmt.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Use JOIN FETCH to load department and position in a single query (avoids N+1)
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.position")
    List<Employee> findAllWithRelations();

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.position WHERE e.deptId = :deptId")
    List<Employee> findByDeptIdWithRelations(@Param("deptId") Integer deptId);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.position WHERE e.empStatus = :status")
    List<Employee> findByEmpStatusWithRelations(@Param("status") String empStatus);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.position WHERE e.empId = :id")
    Optional<Employee> findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.position WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Employee> searchWithRelations(@Param("q") String query);

    List<Employee> findByManagerId(Integer managerId);

    long countByEmpStatus(String empStatus);
}
