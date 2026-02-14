package com.empmgmt.service;

import com.empmgmt.model.Dependent;
import com.empmgmt.repository.DependentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DependentService {

    private final DependentRepository repo;

    public DependentService(DependentRepository repo) { this.repo = repo; }

    @Cacheable(value = "dependents", key = "#empId")
    public List<Dependent> findByEmployee(Integer empId) { return repo.findByEmpId(empId); }

    public Dependent findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Dependent not found: " + id));
    }

    @CacheEvict(value = "dependents", allEntries = true)
    public Dependent create(Dependent dep) { return repo.save(dep); }

    @CacheEvict(value = "dependents", allEntries = true)
    public Dependent update(Integer id, Dependent dep) {
        Dependent existing = findById(id);
        existing.setDepName(dep.getDepName());
        existing.setRelation(dep.getRelation());
        existing.setDob(dep.getDob());
        existing.setGender(dep.getGender());
        return repo.save(existing);
    }

    @CacheEvict(value = "dependents", allEntries = true)
    public void delete(Integer id) { repo.deleteById(id); }
}
