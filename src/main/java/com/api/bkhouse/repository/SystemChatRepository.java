package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.SystemChat;

public interface SystemChatRepository extends JpaRepository<SystemChat, Long> {
}
