package com.empmgmt.repository;

import com.empmgmt.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Integer> {

    @Query("SELECT p FROM Position p LEFT JOIN FETCH p.department")
    List<Position> findAllWithRelations();

    @Query("SELECT p FROM Position p LEFT JOIN FETCH p.department WHERE p.deptId = :deptId")
    List<Position> findByDeptIdWithRelations(@Param("deptId") Integer deptId);

    List<Position> findByIsActive(String isActive);
}
