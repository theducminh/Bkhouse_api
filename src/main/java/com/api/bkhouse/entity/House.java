package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;
import java.time.Instant;

@Entity
@Table(name = "house")
public class House {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "real_estate_post_id")
    private RealEstatePost realEstatePost;

    @Column(name = "no_floors")
    private Integer noFloor;

    @Column(name = "no_bedrooms")
    private Integer noBedroom;

    @Column(name = "no_bathrooms")
    private Integer noBathroom;

    @Column(name = "furniture")
    private String furniture;

    @Enumerated(EnumType.STRING)
    @Column(name = "balcony_direction")
    private EDirection balconyDirection;


    @Column(name = "street_width")
    private Double streetWidth;

    @Column(name = "front_width")
    private Double frontWidth;

    @Column (name = "behind_width")
    private Double behindWidth;

    @Column(name = "house_orientation")
    private String houseOrientation;

    @Column (name = "updated_at")
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

    public Integer getNoFloor() {
        return noFloor;
    }

    public void setNoFloor(Integer noFloor) {
        this.noFloor = noFloor;
    }

    public Integer getNoBedroom() {
        return noBedroom;
    }

    public void setNoBedroom(Integer noBedroom) {
        this.noBedroom = noBedroom;
    }

    public Integer getNoBathroom() {
        return noBathroom;
    }

    public void setNoBathroom(Integer noBathroom) {
        this.noBathroom = noBathroom;
    }

    public String getFurniture() {
        return furniture;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }

    public EDirection getBalconyDirection() {
        return balconyDirection;
    }

    public void setBalconyDirection(EDirection balconyDirection) {
        this.balconyDirection = balconyDirection;
    }
    public String getHouseOrientation() {
        return houseOrientation;
    }

    public void setHouseOrientation(String houseOrientation) {
        this.houseOrientation = houseOrientation;
    }

    public Double getStreetWidth() {
        return streetWidth;
    }

    public void setStreetWidth(Double streetWidth) {
        this.streetWidth = streetWidth;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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
}
