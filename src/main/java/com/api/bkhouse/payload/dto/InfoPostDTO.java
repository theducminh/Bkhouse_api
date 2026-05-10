package com.api.bkhouse.payload.dto;

import java.time.Instant;
import java.util.UUID;

public class InfoPostDTO {
    private Long id;
    private InfoTypeDTO infoType;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private Integer view;
    private UUID createBy;
    private Instant createAt;
    private Instant updateAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InfoTypeDTO getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoTypeDTO infoType) {
        this.infoType = infoType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }
}
