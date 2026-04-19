package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
