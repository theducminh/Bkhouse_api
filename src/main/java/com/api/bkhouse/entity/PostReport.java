package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_report")
public class PostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    @NotBlank
    @NotNull
    private String description;

    @Column(name = "post_id")
    @NotBlank
    @NotNull
    private String postId;

    @Column(name = "is_forum_post")
    @NotNull
    @NotBlank
    private boolean isForumPost;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_at")
    private Instant createAt;

    @Column
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "post_report_type",
            joinColumns = @JoinColumn(name = "post_report_id"),
            inverseJoinColumns = @JoinColumn(name = "report_type_id"))
    private Set<ReportType> reportTypes = new HashSet<>();

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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isForumPost() {
        return isForumPost;
    }

    public void setForumPost(boolean forumPost) {
        isForumPost = forumPost;
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

    public Set<ReportType> getReportTypes() {
        return reportTypes;
    }

    public void setReportTypes(Set<ReportType> reportTypes) {
        this.reportTypes = reportTypes;
    }
}
