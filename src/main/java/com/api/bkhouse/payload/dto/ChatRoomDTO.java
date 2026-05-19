package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChatRoomDTO {
    @NotNull
    private Integer id;

    
    @NotNull
    private UUID firstUserId;
    
    @NotNull
    private UUID secondUserId;
    
    @NotNull
    private boolean enable;
   
    private boolean anonymous;
    private List<MessageDTO> messages;
    private UUID createBy;
    private Instant createAt;
    private Instant updateAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(UUID firstUserId) {
        this.firstUserId = firstUserId;
    }

    public UUID getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(UUID secondUserId) {
        this.secondUserId = secondUserId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public UUID getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UUID createBy) {
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

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}
