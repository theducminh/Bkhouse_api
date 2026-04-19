package com.api.bkhouse.payload.dto;

import java.time.Instant;

public class RealEstatePostPriceDTO {
    private Long id;
    private String realEstatePostId;
    private Double price;
    private String createBy;
    private Instant createAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealEstatePostId() {
        return realEstatePostId;
    }

    public void setRealEstatePostId(String realEstatePostId) {
        this.realEstatePostId = realEstatePostId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
}
