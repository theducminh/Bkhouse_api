package com.api.bkhouse.entity;

import com.api.bkhouse.constant.enumeric.EPostType; // Kế thừa Enum từ thiết kế PostMedia

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_comment")
public class PostComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. CHUYỂN String THÀNH UUID
    @Column(name = "post_id")
    @NotNull(message = "Post ID không được để trống")
    private UUID postId;

    // 2. THAY THẾ isForumPost BẰNG postType (Polymorphism)
    @Column(name = "post_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Post Type không được để trống")
    private EPostType postType;

    // 3. THAY THẾ createBy (String) BẰNG userId (UUID) để làm Khóa ngoại
    @Column(name = "user_id")
    @NotNull(message = "User ID không được để trống")
    private UUID userId;

    @Column(name = "content", columnDefinition = "TEXT")
    @NotNull
    @NotBlank(message = "Nội dung bình luận không được để trống")
    private String content;

    // 4. CHUẨN HÓA TÊN BIẾN (createAt -> createdAt)
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public EPostType getPostType() {
        return postType;
    }

    public void setPostType(EPostType postType) {
        this.postType = postType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}