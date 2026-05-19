package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "post_report")
public class PostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bỏ @NotBlank ở đây để lỡ khách chỉ tick chọn lý do (Lừa đảo) mà lười gõ text thì hệ thống vẫn cho lưu
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Đã chuyển sang UUID để query trên Postgres không bị lỗi lệch kiểu dữ liệu
    @Column(name = "post_id")
    @NotNull
    private UUID postId;

    // Đã nâng cấp từ boolean isForumPost thành postType (VD: NEWS, REAL_ESTATE, FORUM...)
    @Column(name = "post_type")
    @NotBlank
    @NotNull
    private String postType;

    // Đã chuyển sang UUID cho đồng bộ với bảng users
    @Column(name = "create_by")
    private UUID createBy;

    // Thêm trường quản lý trạng thái xử lý (Mặc định là PENDING)
    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "create_at")
    private Instant createAt;

    // Thêm trường lưu thời gian Admin đã giải quyết
    @Column(name = "resolved_at")
    private Instant resolvedAt;

    // VẪN GIỮ LẠI bảng trung gian để lưu danh sách các mã lỗi khách tick chọn
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "post_report_type",
            joinColumns = @JoinColumn(name = "post_report_id"),
            inverseJoinColumns = @JoinColumn(name = "report_type_id"))
    private Set<ReportType> reportTypes = new HashSet<>();

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Set<ReportType> getReportTypes() {
        return reportTypes;
    }

    public void setReportTypes(Set<ReportType> reportTypes) {
        this.reportTypes = reportTypes;
    }
}