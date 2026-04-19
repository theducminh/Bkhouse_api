package com.api.bkhouse.payload.dto.post;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;
import com.api.bkhouse.constant.enumeric.EStatus;
import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RealEstatePostDTO {

    @NotNull
    private UUID id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EType type;

    @NotNull
    private UserDTO ownerId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String addressShow;

    @NotNull
    private Double area;

    @NotNull
    private Double price;

    @NotNull
    private ProvinceDTO province;

    @NotNull
    private DistrictDTO district;

    @NotNull
    private WardDTO ward;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EStatus status;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    @NotNull
    private boolean enable;

    @NotNull
    private Integer priority;

    @NotNull
    private Integer period;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EDirection direction;

    @NotNull
    private boolean sell;

    @NotNull
    private String street;

    private Integer view;

    private Integer clickedView;

    private UUID createBy;

    private Instant createAt;

    private UUID updateBy;

    private Instant updateAt;

    private List<RealEstatePostPriceDTO> realEstatePostPrices;

    public List<RealEstatePostPriceDTO> getRealEstatePostPrices() {
        return realEstatePostPrices;
    }

    public void setRealEstatePostPrices(List<RealEstatePostPriceDTO> realEstatePostPrices) {
        this.realEstatePostPrices = realEstatePostPrices;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EType getType() {
        return type;
    }

    public void setType(EType type) {
        this.type = type;
    }

    public UserDTO getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserDTO ownerId) {
        this.ownerId = ownerId;
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

    public String getAddressShow() {
        return addressShow;
    }

    public void setAddressShow(String addressShow) {
        this.addressShow = addressShow;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ProvinceDTO getProvince() {
        return province;
    }

    public void setProvince(ProvinceDTO province) {
        this.province = province;
    }

    public DistrictDTO getDistrict() {
        return district;
    }

    public void setDistrict(DistrictDTO district) {
        this.district = district;
    }

    public WardDTO getWard() {
        return ward;
    }

    public void setWard(WardDTO ward) {
        this.ward = ward;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public EDirection getDirection() {
        return direction;
    }

    public void setDirection(EDirection direction) {
        this.direction = direction;
    }

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
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

    public UUID getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(UUID updateBy) {
        this.updateBy = updateBy;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public Integer getClickedView() {
        return clickedView;
    }

    public void setClickedView(Integer clickedView) {
        this.clickedView = clickedView;
    }
}
