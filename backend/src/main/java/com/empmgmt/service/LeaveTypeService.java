package com.empmgmt.service;

import com.empmgmt.model.LeaveType;
import com.empmgmt.repository.LeaveTypeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository repo;

    public LeaveTypeService(LeaveTypeRepository repo) { this.repo = repo; }

    @Cacheable("leaveTypes")
    public List<LeaveType> findAll() { return repo.findAll(); }

    public List<LeaveType> findActive() { return repo.findByIsActive("Y"); }

    public LeaveType findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Leave type not found: " + id));
    }

    @CacheEvict(value = "leaveTypes", allEntries = true)
    public LeaveType create(LeaveType lt) { return repo.save(lt); }

    @CacheEvict(value = "leaveTypes", allEntries = true)
    public LeaveType update(Integer id, LeaveType lt) {
        LeaveType existing = findById(id);
        existing.setLvTypeName(lt.getLvTypeName());
        existing.setLvTypeDesc(lt.getLvTypeDesc());
        existing.setMaxDays(lt.getMaxDays());
        existing.setIsPaid(lt.getIsPaid());
        existing.setIsActive(lt.getIsActive());
        return repo.save(existing);
    }

    @CacheEvict(value = "leaveTypes", allEntries = true)
    public void delete(Integer id) { repo.deleteById(id); }
}
