package com.api.bkhouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.api.bkhouse.entity.RealEstatePost;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "real_estate_post_price")
public class RealEstatePostPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "real_estate_post_id", referencedColumnName = "id", updatable = false)
    @JsonIgnore
    private RealEstatePost realEstatePost;

    @Column(name = "price")
    @NotNull
    private Double price;

    @Column(name = "create_by")
    private UUID createBy;

    @Column(name = "create_at")
    private Instant createAt;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
