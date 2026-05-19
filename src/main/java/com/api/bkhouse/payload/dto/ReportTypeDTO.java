package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class ReportTypeDTO {
    private Integer id;

    // ĐÃ THÊM: Rào chắn bắt buộc phải nhập tên loại vi phạm (chỉ dùng cho String là chuẩn bài)
    @NotBlank
    @NotNull
    private String name;

    private boolean isForum;
    private UUID createBy;
    private Instant createAt;
    private UUID updateBy;
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