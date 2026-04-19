package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.House;

import java.util.Optional;
import java.util.UUID;

public interface HouseRepository extends JpaRepository<House, Long> {
    Optional<House> findByRealEstatePostId(UUID realEstatePostId);
    void deleteByRealEstatePostId(UUID realEstatePostId);
}
