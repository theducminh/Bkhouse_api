package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.SpecialAccountPay;

import java.util.List;
import java.util.UUID;

public interface SpecialAccountPayRepository extends JpaRepository<SpecialAccountPay, Long> {
    List<SpecialAccountPay> findByUserIdOrderByCreateAtDesc(UUID userId);
}
