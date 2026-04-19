package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.PostView;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
}
