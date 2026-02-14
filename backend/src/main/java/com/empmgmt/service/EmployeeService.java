package com.empmgmt.service;

import com.empmgmt.model.Employee;
import com.empmgmt.repository.EmployeeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) { this.repo = repo; }

    @Cacheable("employees")
    public List<Employee> findAll() { return repo.findAllWithRelations(); }

    public List<Employee> findByDept(Integer deptId) { return repo.findByDeptIdWithRelations(deptId); }
    public List<Employee> findByStatus(String status) { return repo.findByEmpStatusWithRelations(status); }

    public List<Employee> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        return repo.searchWithRelations(query);
    }

    @Cacheable(value = "employeeById", key = "#id")
    public Employee findById(Integer id) {
        return repo.findByIdWithRelations(id).orElseThrow(() -> new RuntimeException("Employee not found: " + id));
    }

    @Caching(evict = {
        @CacheEvict(value = "employees", allEntries = true),
        @CacheEvict(value = "employeeById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Employee create(Employee emp) { return repo.save(emp); }

    @Caching(evict = {
        @CacheEvict(value = "employees", allEntries = true),
        @CacheEvict(value = "employeeById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Employee update(Integer id, Employee emp) {
        Employee existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Employee not found: " + id));
        existing.setFirstName(emp.getFirstName());
        existing.setLastName(emp.getLastName());
        existing.setMiddleName(emp.getMiddleName());
        existing.setEmail(emp.getEmail());
        existing.setHireDate(emp.getHireDate());
        existing.setTermDate(emp.getTermDate());
        existing.setDeptId(emp.getDeptId());
        existing.setPosId(emp.getPosId());
        existing.setManagerId(emp.getManagerId());
        existing.setEmpStatus(emp.getEmpStatus());
        existing.setAddr1(emp.getAddr1());
        existing.setAddr2(emp.getAddr2());
        existing.setCity(emp.getCity());
        existing.setState(emp.getState());
        existing.setZipCode(emp.getZipCode());
        existing.setCountry(emp.getCountry());
        existing.setDob(emp.getDob());
        existing.setGender(emp.getGender());
        return repo.save(existing);
    }

    @Caching(evict = {
        @CacheEvict(value = "employees", allEntries = true),
        @CacheEvict(value = "employeeById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void delete(Integer id) { repo.deleteById(id); }

    public long count() { return repo.count(); }
    public long countByStatus(String status) { return repo.countByEmpStatus(status); }
}
