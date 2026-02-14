package com.empmgmt.service;

import com.empmgmt.model.LeaveReq;
import com.empmgmt.repository.LeaveReqRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class LeaveReqService {

    private final LeaveReqRepository repo;

    public LeaveReqService(LeaveReqRepository repo) { this.repo = repo; }

    @Cacheable("leaveReqs")
    public List<LeaveReq> findAll() { return repo.findAllWithRelations(); }

    public List<LeaveReq> findByEmployee(Integer empId) { return repo.findByEmpIdWithRelations(empId); }
    public List<LeaveReq> findByStatus(String status) { return repo.findByStatusWithRelations(status); }
    public List<LeaveReq> findPending() { return repo.findByStatusWithRelations("PENDING"); }

    public LeaveReq findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Leave request not found: " + id));
    }

    @Caching(evict = {
        @CacheEvict(value = "leaveReqs", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public LeaveReq create(LeaveReq req) {
        req.setStatus("PENDING");
        return repo.save(req);
    }

    @Caching(evict = {
        @CacheEvict(value = "leaveReqs", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public LeaveReq update(Integer id, LeaveReq req) {
        LeaveReq existing = findById(id);
        existing.setLvTypeId(req.getLvTypeId());
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());
        existing.setTotalDays(req.getTotalDays());
        existing.setReason(req.getReason());
        existing.setStatus(req.getStatus());
        existing.setComments(req.getComments());
        return repo.save(existing);
    }

    @Caching(evict = {
        @CacheEvict(value = "leaveReqs", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public LeaveReq approve(Integer id, Integer approverId, String comments) {
        LeaveReq req = findById(id);
        req.setStatus("APPROVED");
        req.setApproverId(approverId);
        req.setApprovedAt(new Timestamp(System.currentTimeMillis()));
        req.setComments(comments);
        return repo.save(req);
    }

    @Caching(evict = {
        @CacheEvict(value = "leaveReqs", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public LeaveReq reject(Integer id, Integer approverId, String comments) {
        LeaveReq req = findById(id);
        req.setStatus("REJECTED");
        req.setApproverId(approverId);
        req.setApprovedAt(new Timestamp(System.currentTimeMillis()));
        req.setComments(comments);
        return repo.save(req);
    }

    @Caching(evict = {
        @CacheEvict(value = "leaveReqs", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void delete(Integer id) { repo.deleteById(id); }

    public long countPending() { return repo.countByStatus("PENDING"); }
}
