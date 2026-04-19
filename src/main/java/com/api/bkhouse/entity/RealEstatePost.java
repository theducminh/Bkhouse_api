package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EDirection;
import com.api.bkhouse.constant.enumeric.EStatus;
import com.api.bkhouse.constant.enumeric.EType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "real_estate_post")
public class RealEstatePost {
    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", updatable = false)
    @NotNull
    @NotBlank
    private EType type;

    @OneToOne
    @JoinColumn(name = "owner_id", updatable = false)
    private User ownerId;

    @Column(name = "title")
    @NotNull
    @NotBlank
    private String title;

    @Column(name = "description")
    @NotNull
    @NotBlank
    private String description;

    @Column(name = "address_show")
    @NotNull
    @NotBlank
    private String addressShow;

    @Column(name = "area")
    @NotNull
    @NotBlank
    private Double area;

    @Column(name = "price")
    @NotNull
    @NotBlank
    private Double price;

    @OneToOne
    @JoinColumn(name = "province_code")
    @NotNull
    @NotBlank
    private Province province;

    @OneToOne
    @JoinColumn(name = "district_code")
    @NotNull
    @NotBlank
    private District district;

    @OneToOne
    @JoinColumn(name = "ward_code")
    @NotNull
    @NotBlank
    private Ward ward;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    @NotBlank
    private EStatus status;

    @Column(name = "lat")
    @NotNull
    @NotBlank
    private Double lat;

    @Column(name = "lng")
    @NotNull
    @NotBlank
    private Double lng;

    @Column(name = "enable")
    @NotNull
    @NotBlank
    private boolean enable;

    @Column(name = "priority")
    @NotNull
    @NotBlank
    private Integer priority;

    @Column(name = "period")
    @NotNull
    @NotBlank
    private Integer period;

    @Column(name = "street")
    @NotNull
    @NotBlank
    private String street;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    @NotNull
    @NotBlank
    private EDirection direction;

    @Column(name = "view")
    private Integer view;

    @Column(name = "clicked_view")
    private Integer clickedView;

    @Column(name = "is_sell", updatable = false)
    private boolean sell;

    @Column(name = "create_by", updatable = false)
    private UUID createBy;

    @Column(name = "create_at", updatable = false)
    private Instant createAt;

    @Column(name = "update_by")
    private UUID updateBy;

    @Column(name = "update_at")
    private Instant updateAt;

    @OneToMany(mappedBy = "realEstatePost")
    private List<RealEstatePostPrice> realEstatePostPrices;

    public List<RealEstatePostPrice> getRealEstatePostPrices() {
        return realEstatePostPrices;
    }

    public void setRealEstatePostPrices(List<RealEstatePostPrice> realEstatePostPrices) {
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

    public User getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(User ownerId) {
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

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
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
