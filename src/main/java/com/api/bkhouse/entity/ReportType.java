package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_type")
public class ReportType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotBlank
    @NotNull
    private String name;

    // ĐÃ SỬA: Xóa @NotBlank và @NotNull vì đây là kiểu boolean nguyên thủy
    @Column(name = "is_forum")
    private boolean isForum;

    // ĐÃ SỬA: Chuyển String thành UUID để đồng bộ với Database Postgres
    @Column(name = "create_by")
    private UUID createBy;

    @Column(name = "create_at")
    private Instant createAt;

    // ĐÃ SỬA: Chuyển String thành UUID
    @Column(name = "update_by")
    private UUID updateBy;

    @Column(name = "update_at")
    private Instant updateAt;

    // ================= GETTERS & SETTERS =================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isForum() {
        return isForum;
    }

    public void setForum(boolean forum) {
        this.isForum = forum;
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

    public UUID getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(UUID updateBy) {
        this.updateBy = updateBy;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }
}