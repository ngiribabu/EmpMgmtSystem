package com.empmgmt.service;

import com.empmgmt.model.Attendance;
import com.empmgmt.repository.AttendanceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository repo;

    public AttendanceService(AttendanceRepository repo) { this.repo = repo; }

    @Cacheable("attendance")
    public List<Attendance> findAll() { return repo.findAll(); }

    public List<Attendance> findByEmployee(Integer empId) { return repo.findByEmpId(empId); }
    public List<Attendance> findByDate(Date date) { return repo.findByWorkDate(date); }

    public List<Attendance> findByEmployeeAndDateRange(Integer empId, Date start, Date end) {
        return repo.findByEmpIdAndWorkDateBetween(empId, start, end);
    }

    public Attendance findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Attendance not found: " + id));
    }

    @CacheEvict(value = "attendance", allEntries = true)
    public Attendance create(Attendance att) { return repo.save(att); }

    @CacheEvict(value = "attendance", allEntries = true)
    public Attendance update(Integer id, Attendance att) {
        Attendance existing = findById(id);
        existing.setWorkDate(att.getWorkDate());
        existing.setClockIn(att.getClockIn());
        existing.setClockOut(att.getClockOut());
        existing.setHrsWorked(att.getHrsWorked());
        existing.setOtHrs(att.getOtHrs());
        existing.setStatus(att.getStatus());
        existing.setNotes(att.getNotes());
        return repo.save(existing);
    }

    @CacheEvict(value = "attendance", allEntries = true)
    public void delete(Integer id) { repo.deleteById(id); }
}
