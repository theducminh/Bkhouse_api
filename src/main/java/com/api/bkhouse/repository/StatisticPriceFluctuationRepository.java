package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.entity.StatisticPriceFluctuation;

import java.util.Optional;

@Repository
public interface StatisticPriceFluctuationRepository extends JpaRepository<StatisticPriceFluctuation, Long> {
}
