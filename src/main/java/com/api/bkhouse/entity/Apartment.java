package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;
import java.time.Instant;

@Entity
@Table(name = "apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "real_estate_post_id", updatable = false)
    private RealEstatePost realEstatePost;

    @Column(name = "floor_no")
    @NotNull
    private Integer floorNo;

    @Column(name = "no_bedroom")
    @NotNull
    private Integer noBedroom;

    @Column(name = "no_bathroom")
    @NotNull
    private Integer noBathroom;

    @Column(name = "furniture")
    @NotNull
    private String furniture;

    @Enumerated(EnumType.STRING)
    @Column(name = "balcony_direction")
    @NotNull
    private EDirection balconyDirection;

    @Column(name = "construction")
    @NotNull
    @NotBlank
    private String construction;

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

    public Integer getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(Integer floorNo) {
        this.floorNo = floorNo;
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

    public String getConstruction() {
        return construction;
    }

    public void setConstruction(String construction) {
        this.construction = construction;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
