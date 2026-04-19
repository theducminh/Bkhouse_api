package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class PostReportDTO {
    @NotBlank
    @NotNull
    private Long id;
    @NotBlank
    @NotNull
    private String description;
    @NotBlank
    @NotNull
    private UUID postId;
    @NotBlank
    @NotNull
    private boolean isForumPost;
    private UUID createBy;
    private Instant createAt;
    private Set<ReportTypeDTO> reportTypes;

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

    public boolean isForumPost() {
        return isForumPost;
    }

    public void setForumPost(boolean forumPost) {
        isForumPost = forumPost;
    }

    public Set<ReportTypeDTO> getReportTypes() {
        return reportTypes;
    }

    public void setReportTypes(Set<ReportTypeDTO> reportTypes) {
        this.reportTypes = reportTypes;
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
}
