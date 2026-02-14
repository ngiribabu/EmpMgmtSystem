package com.empmgmt.repository;

import com.empmgmt.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.sql.Date;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByEmpId(Integer empId);
    List<Attendance> findByEmpIdAndWorkDateBetween(Integer empId, Date start, Date end);
    List<Attendance> findByWorkDate(Date workDate);
}
