package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.SpecialAccountPay;

import java.util.List;

public interface SpecialAccountPayRepository extends JpaRepository<SpecialAccountPay, Long> {
    List<SpecialAccountPay> findByUserIdOrderByCreateAtDesc(String userId);
}
