package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.bkhouse.entity.ProjectParam;
import java.util.UUID;

public interface ProjectParamRepository extends JpaRepository<ProjectParam, Long> {
    boolean existsByIdAndProjectId(Long id, UUID projectId);
    @Query(value = "select count(p.id) " +
            "from project_param pp inner join project p on pp.project_id = p.id " +
            "where pp.id = :id " +
            "and p.create_by = :userId", nativeQuery = true)
    long paramIsBelongToUser(Long id, UUID userId);
}
