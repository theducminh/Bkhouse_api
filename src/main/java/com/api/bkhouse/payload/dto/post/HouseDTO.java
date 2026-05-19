package com.api.bkhouse.payload.dto.post;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;


public class HouseDTO extends BasePost {
    
    private Long id;

    @NotNull
    private Integer noFloor;

    @NotNull
    private Integer noBedroom;

    @NotNull
    private Integer noBathroom;

    @NotNull
    private String furniture;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EDirection balconyDirection;

    @NotNull
    private Double frontWidth;

    @NotNull
    private Double behindWidth;

    @NotNull
    private Double streetWidth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getStreetWidth() {
        return streetWidth;
    }

    public void setStreetWidth(Double streetWidth) {
        this.streetWidth = streetWidth;
    }
}
