package com.empmgmt.service;

import com.empmgmt.model.Salary;
import com.empmgmt.repository.SalaryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SalaryService {

    private final SalaryRepository repo;

    public SalaryService(SalaryRepository repo) { this.repo = repo; }

    public List<Salary> findAll() { return repo.findAll(); }

    @Cacheable(value = "salaries", key = "#empId")
    public List<Salary> findByEmployee(Integer empId) { return repo.findByEmpIdOrderByEffDateDesc(empId); }

    public Salary findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Salary not found: " + id));
    }

    @CacheEvict(value = "salaries", allEntries = true)
    public Salary create(Salary salary) {
        repo.findByEmpIdAndIsCurrent(salary.getEmpId(), "Y").ifPresent(prev -> {
            prev.setIsCurrent("N");
            prev.setEndDate(salary.getEffDate());
            repo.save(prev);
        });
        salary.setIsCurrent("Y");
        return repo.save(salary);
    }

    @CacheEvict(value = "salaries", allEntries = true)
    public Salary update(Integer id, Salary salary) {
        Salary existing = findById(id);
        existing.setBaseSalary(salary.getBaseSalary());
        existing.setBonus(salary.getBonus());
        existing.setCurrency(salary.getCurrency());
        existing.setPayFreq(salary.getPayFreq());
        existing.setEffDate(salary.getEffDate());
        existing.setEndDate(salary.getEndDate());
        existing.setIsCurrent(salary.getIsCurrent());
        existing.setReason(salary.getReason());
        existing.setNotes(salary.getNotes());
        return repo.save(existing);
    }

    @CacheEvict(value = "salaries", allEntries = true)
    public void delete(Integer id) { repo.deleteById(id); }
}
