package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.InfoType;

import java.util.List;

@Repository
public interface InfoTypeRepository extends JpaRepository<InfoType, Integer> {
    List<InfoType> findByIdGreaterThanEqual(Integer id);
}
