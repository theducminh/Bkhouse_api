package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.Apartment;

import java.util.Optional;
import java.util.UUID;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findByRealEstatePostId(UUID realEstatePostId);
    void deleteByRealEstatePostId(UUID realEstatePostId);
}
