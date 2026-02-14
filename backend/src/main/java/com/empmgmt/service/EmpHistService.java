package com.empmgmt.service;

import com.empmgmt.model.EmpHist;
import com.empmgmt.repository.EmpHistRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpHistService {

    private final EmpHistRepository repo;

    public EmpHistService(EmpHistRepository repo) { this.repo = repo; }

    @Cacheable(value = "empHist", key = "#empId")
    public List<EmpHist> findByEmployee(Integer empId) { return repo.findByEmpIdOrderByEffDateDesc(empId); }

    public EmpHist findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("History not found: " + id));
    }

    @CacheEvict(value = "empHist", allEntries = true)
    public EmpHist create(EmpHist hist) { return repo.save(hist); }

    @CacheEvict(value = "empHist", allEntries = true)
    public void delete(Integer id) { repo.deleteById(id); }
}
