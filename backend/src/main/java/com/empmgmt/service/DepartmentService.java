package com.empmgmt.service;

import com.empmgmt.model.Department;
import com.empmgmt.repository.DepartmentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) { this.repo = repo; }

    @Cacheable("departments")
    public List<Department> findAll() { return repo.findAll(); }

    public List<Department> findActive() { return repo.findByIsActive("Y"); }

    @Cacheable(value = "departmentById", key = "#id")
    public Department findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Department not found: " + id));
    }

    @Caching(evict = {
        @CacheEvict(value = "departments", allEntries = true),
        @CacheEvict(value = "departmentById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Department create(Department dept) { return repo.save(dept); }

    @Caching(evict = {
        @CacheEvict(value = "departments", allEntries = true),
        @CacheEvict(value = "departmentById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Department update(Integer id, Department dept) {
        Department existing = findById(id);
        existing.setDeptName(dept.getDeptName());
        existing.setDeptDesc(dept.getDeptDesc());
        existing.setLocation(dept.getLocation());
        existing.setManagerId(dept.getManagerId());
        existing.setIsActive(dept.getIsActive());
        return repo.save(existing);
    }

    @Caching(evict = {
        @CacheEvict(value = "departments", allEntries = true),
        @CacheEvict(value = "departmentById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void delete(Integer id) { repo.deleteById(id); }

    public long count() { return repo.count(); }
}
