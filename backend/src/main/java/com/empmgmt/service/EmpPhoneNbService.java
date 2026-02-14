package com.empmgmt.service;

import com.empmgmt.model.EmpPhoneNb;
import com.empmgmt.repository.EmpPhoneNbRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpPhoneNbService {

    private final EmpPhoneNbRepository repo;

    public EmpPhoneNbService(EmpPhoneNbRepository repo) { this.repo = repo; }

    @Cacheable(value = "phones", key = "#empId")
    public List<EmpPhoneNb> findByEmployee(Integer empId) { return repo.findByEmpId(empId); }

    public EmpPhoneNb findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Phone not found: " + id));
    }

    @CacheEvict(value = "phones", allEntries = true)
    public EmpPhoneNb create(EmpPhoneNb phone) { return repo.save(phone); }

    @CacheEvict(value = "phones", allEntries = true)
    public EmpPhoneNb update(Integer id, EmpPhoneNb phone) {
        EmpPhoneNb existing = findById(id);
        existing.setPhoneType(phone.getPhoneType());
        existing.setPhoneNum(phone.getPhoneNum());
        existing.setIsPrimary(phone.getIsPrimary());
        return repo.save(existing);
    }

    @CacheEvict(value = "phones", allEntries = true)
    public void delete(Integer id) { repo.deleteById(id); }
}
