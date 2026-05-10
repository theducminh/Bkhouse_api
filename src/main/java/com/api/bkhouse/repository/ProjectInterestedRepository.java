package com.api.bkhouse.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.ProjectInterested;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectInterestedRepository extends JpaRepository<ProjectInterested, Long> {
    boolean existsByDeviceInfoAndProjectId(String deviceInfo, UUID projectId);
    boolean existsByUserIdAndProjectId(UUID userId, UUID projectId);
    Optional<ProjectInterested> findByDeviceInfoAndProjectId(String deviceInfo, UUID projectId);
    Optional<ProjectInterested> findByUserIdAndProjectId(UUID userId, UUID projectId);
    List<ProjectInterested> findByUserId(UUID userId, Sort sort);
}
