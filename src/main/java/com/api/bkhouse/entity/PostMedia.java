package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "post_media", indexes = {
    @Index(name = "idx_post_media_post_id", columnList = "post_id")
})
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // KHÔNG dùng @ManyToOne ở đây vì nó trỏ tới 2 bảng khác nhau. 
    // Chỉ lưu ID dưới dạng biến bình thường.
    @Column(name = "post_id", nullable = false)
    @NotNull(message = "ID bài viết không được để trống")
    private UUID postId;

    @Column(name = "media_url", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Đường dẫn media không được để trống")
    private String mediaUrl;

    @Column(name = "media_type", length = 20, nullable = false)
    @NotBlank(message = "Loại media không được để trống")
    private String mediaType;

    @Column(name = "is_thumbnail")
    private Boolean isThumbnail;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Boolean getIsThumbnail() {
        return isThumbnail; // Spring Data JPA sẽ tự map với is_thumbnail
    }

    public void setIsThumbnail(Boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }
}