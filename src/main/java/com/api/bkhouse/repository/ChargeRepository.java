package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.Charge;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge,Long> {
    List<Charge> findByUserIdOrderByCreateAtDesc(String userId);
}
