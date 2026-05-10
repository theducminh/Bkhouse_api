package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.entity.ChatRoom;

import java.time.Instant;
import java.util.UUID;

public class MessageDTO {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    private String message;
    @NotNull
    private Integer chatRoomId;
    private String createBy;
    private Instant createAt;
    private Instant updateAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Integer chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }
}
