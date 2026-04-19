package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.PriceFluctuation;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceFluctuationRepository extends JpaRepository<PriceFluctuation, Long> {
    List<PriceFluctuation> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
