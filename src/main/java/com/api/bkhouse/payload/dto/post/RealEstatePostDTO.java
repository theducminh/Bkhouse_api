package com.api.bkhouse.payload.dto.post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point; 
import com.api.bkhouse.constant.enumeric.EDirection;
import com.api.bkhouse.constant.enumeric.EStatus;
import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.payload.dto.*;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RealEstatePostDTO {

    private UUID id;

    @NotNull(message = "Loại hình không được để trống")
    private EType type;

    @NotNull(message = "Người đăng không được để trống")
    private UserDTO owner;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotBlank(message = "Địa chỉ hiển thị không được để trống")
    private String addressShow;

    @NotNull(message = "Diện tích không được để trống")
    private Double area;

    @NotNull(message = "Giá không được để trống")
    private Double price;

    @NotNull(message = "Tỉnh/Thành phố không được để trống")
    private ProvinceDTO province;

    @NotNull(message = "Quận/Huyện không được để trống")
    private DistrictDTO district;

    @NotNull(message = "Phường/Xã không được để trống")
    private WardDTO ward;

    @NotNull(message = "Trạng thái không được để trống")
    private EStatus status;

    // LƯU Ý KỸ THUẬT: 
    // DTO có thể nhận String (ví dụ dạng "POINT(105.8 21.0)") từ Frontend
    // Sau đó Backend (Service/Mapper) sẽ convert String này thành org.locationtech.jts.geom.Point
    private Point location; 

    @NotNull(message = "Trạng thái hiển thị không được để trống")
    private Boolean isEnabled;

    @NotNull(message = "Trạng thái bán/cho thuê không được để trống")
    private Boolean isSell;

    private Integer priority;

    private EDirection direction;

    private String street;

    private Integer viewCount;

    private Integer contactCount;

    private String metadata; 

    private UUID createdBy;

    private Instant createdAt;

    private Instant updatedAt;

    private List<RealEstatePostPriceDTO> realEstatePostPrices;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

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
        return owner;
    }

    public void setOwnerId(UserDTO owner) {
        this.owner = owner;
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

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getIsSell() {
        return isSell;
    }

    public void setIsSell(Boolean isSell) {
        this.isSell = isSell;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public EDirection getDirection() {
        return direction;
    }

    public void setDirection(EDirection direction) {
        this.direction = direction;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getContactCount() {
        return contactCount;
    }

    public void setContactCount(Integer contactCount) {
        this.contactCount = contactCount;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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

    public List<RealEstatePostPriceDTO> getRealEstatePostPrices() {
        return realEstatePostPrices;
    }

    public void setRealEstatePostPrices(List<RealEstatePostPriceDTO> realEstatePostPrices) {
        this.realEstatePostPrices = realEstatePostPrices;
    }
}