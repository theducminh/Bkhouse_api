package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;

@Entity
@Table(name = "apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "real_estate_post_id", updatable = false)
    private RealEstatePost realEstatePost;

    @Column(name = "floor_no")
    @NotNull
    @NotBlank
    private Integer floorNo;

    @Column(name = "no_bedroom")
    @NotNull
    @NotBlank
    private Integer noBedroom;

    @Column(name = "no_bathroom")
    @NotNull
    @NotBlank
    private Integer noBathroom;

    @Column(name = "furniture")
    @NotNull
    @NotBlank
    private String furniture;

    @Enumerated(EnumType.STRING)
    @Column(name = "balcony_direction")
    @NotNull
    @NotBlank
    private EDirection balconyDirection;

    @Column(name = "construction")
    @NotNull
    @NotBlank
    private String construction;

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
}
