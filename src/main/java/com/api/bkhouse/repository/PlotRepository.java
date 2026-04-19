package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.Plot;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlotRepository extends JpaRepository<Plot, Long> {
    Optional<Plot> findByRealEstatePostId(UUID realEstatePostId);
    void deleteByRealEstatePostId(UUID realEstatePostId);
}
