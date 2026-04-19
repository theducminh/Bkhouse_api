package com.api.bkhouse.payload.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ForumPostDTO {
    private UUID id;
    private String content;
    private UUID createBy;
    private Instant createAt;
    private UUID updateBy;
    private Instant updateAt;
    private List<PostMediaDTO> postMedia;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public List<PostMediaDTO> getPostMedia() {
        return postMedia;
    }

    public void setPostMedia(List<PostMediaDTO> postMedia) {
        this.postMedia = postMedia;
    }
}
