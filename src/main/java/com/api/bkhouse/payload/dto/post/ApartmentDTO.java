package com.api.bkhouse.payload.dto.post;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;

public class ApartmentDTO extends BasePost{
    @NotNull
    @NotBlank
    private Long id;

    @NotNull
    @NotBlank
    private Integer floorNo;

    @NotNull
    @NotBlank
    private Integer noBedroom;

    @NotNull
    @NotBlank
    private Integer noBathroom;

    @NotNull
    @NotBlank
    private String furniture;

    @Enumerated(EnumType.STRING)
    @NotNull
    @NotBlank
    private EDirection balconyDirection;

    @NotNull
    @NotBlank
    private String construction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
