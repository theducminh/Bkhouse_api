package com.api.bkhouse.entity;

import java.time.Instant;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "plot")
public class Plot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "real_estate_post_id")
    private RealEstatePost realEstatePost;

    @Column(name = "front_width")
    private Double frontWidth;

    @Column(name = "behind_width")
    private Double behindWidth;

    @Column(name = "length")
    private Double length;

    @Column(name = "shape_description", columnDefinition = "TEXT")
    private String shapeDescription;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RealEstatePost getRealEstatePost() {
        return realEstatePost;
    }

    public void setRealEstatePost(RealEstatePost realEstatePost) {
        this.realEstatePost = realEstatePost;
    }

    public Double getFrontWidth() {
        return frontWidth;
    }

    public void setFrontWidth(Double frontWidth) {
        this.frontWidth = frontWidth;
    }

    public Double getBehindWidth() {
        return behindWidth;
    }

    public void setBehindWidth(Double behindWidth) {
        this.behindWidth = behindWidth;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getShapeDescription() {
        return shapeDescription;
    }

    public void setShapeDescription(String shapeDescription) {
        this.shapeDescription = shapeDescription;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
