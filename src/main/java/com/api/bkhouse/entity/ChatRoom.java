package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
        
    @Column(name = "first_user_id")
    @NotNull
    private UUID firstUserId;

    @Column(name = "second_user_id")
    @NotNull
    private UUID secondUserId;

    @Column(name = "is_active")
    private boolean enable;

    @Column(name = "created_by", updatable = false)
    private UUID createBy;

    @Column(name = "created_at", updatable = false)
    private Instant createAt;

    @Column(name = "updated_at")
    private Instant updateAt;

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages;

    @Column(name = "is_anonymous")
    private boolean anonymous;

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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}
