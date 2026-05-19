package com.api.bkhouse.payload.dto.post;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;

public class ApartmentDTO extends BasePost{
    
    private Long id;

    @NotNull
    private Integer floorNo;

    @NotNull
    private Integer noBedroom;

    @NotNull
    private Integer noBathroom;

    @NotNull
    private String furniture;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EDirection balconyDirection;

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
