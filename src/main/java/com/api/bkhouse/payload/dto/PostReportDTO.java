package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class PostReportDTO {
    
    // Bỏ hết Validate ở ID vì lúc tạo mới (Create) thì ID sẽ là null
    private Long id;
    
    // Bỏ Validate ở đây để khách chỉ cần tick chọn lý do, lười gõ chữ vẫn report được
    private String description;
    
    // BỎ @NotBlank đi, UUID chỉ dùng @NotNull thôi
    @NotNull
    private UUID postId;
    
    // ĐÃ SỬA: Đổi isForumPost thành postType (Chuẩn String thì mới dùng đc @NotBlank)
    @NotBlank
    private String postType;
    
    private UUID createBy;
    private Instant createAt;
    
    // ĐÃ THÊM: Quản lý trạng thái
    private String status;
    private Instant resolvedAt;
    
    private Set<ReportTypeDTO> reportTypes;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Set<ReportTypeDTO> getReportTypes() {
        return reportTypes;
    }

    public void setReportTypes(Set<ReportTypeDTO> reportTypes) {
        this.reportTypes = reportTypes;
    }
}