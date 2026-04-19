package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.About;

@Repository
public interface AboutRepository extends JpaRepository<About, Integer> {
}
