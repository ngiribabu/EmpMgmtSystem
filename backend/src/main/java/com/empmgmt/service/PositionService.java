package com.empmgmt.service;

import com.empmgmt.model.Position;
import com.empmgmt.repository.PositionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PositionService {

    private final PositionRepository repo;

    public PositionService(PositionRepository repo) { this.repo = repo; }

    @Cacheable("positions")
    public List<Position> findAll() { return repo.findAllWithRelations(); }

    public List<Position> findByDept(Integer deptId) { return repo.findByDeptIdWithRelations(deptId); }
    public List<Position> findActive() { return repo.findByIsActive("Y"); }

    @Cacheable(value = "positionById", key = "#id")
    public Position findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Position not found: " + id));
    }

    @Caching(evict = {
        @CacheEvict(value = "positions", allEntries = true),
        @CacheEvict(value = "positionById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Position create(Position pos) { return repo.save(pos); }

    @Caching(evict = {
        @CacheEvict(value = "positions", allEntries = true),
        @CacheEvict(value = "positionById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Position update(Integer id, Position pos) {
        Position existing = findById(id);
        existing.setPosTitle(pos.getPosTitle());
        existing.setPosDesc(pos.getPosDesc());
        existing.setDeptId(pos.getDeptId());
        existing.setMinSalary(pos.getMinSalary());
        existing.setMaxSalary(pos.getMaxSalary());
        existing.setIsActive(pos.getIsActive());
        return repo.save(existing);
    }

    @Caching(evict = {
        @CacheEvict(value = "positions", allEntries = true),
        @CacheEvict(value = "positionById", allEntries = true),
        @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void delete(Integer id) { repo.deleteById(id); }

    public long count() { return repo.count(); }
}
