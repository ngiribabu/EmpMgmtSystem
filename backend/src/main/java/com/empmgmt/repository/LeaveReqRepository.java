package com.empmgmt.repository;

import com.empmgmt.model.LeaveReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LeaveReqRepository extends JpaRepository<LeaveReq, Integer> {

    @Query("SELECT lr FROM LeaveReq lr LEFT JOIN FETCH lr.leaveType")
    List<LeaveReq> findAllWithRelations();

    @Query("SELECT lr FROM LeaveReq lr LEFT JOIN FETCH lr.leaveType WHERE lr.empId = :empId")
    List<LeaveReq> findByEmpIdWithRelations(@Param("empId") Integer empId);

    @Query("SELECT lr FROM LeaveReq lr LEFT JOIN FETCH lr.leaveType WHERE lr.status = :status")
    List<LeaveReq> findByStatusWithRelations(@Param("status") String status);

    List<LeaveReq> findByApproverId(Integer approverId);
    long countByStatus(String status);
}
