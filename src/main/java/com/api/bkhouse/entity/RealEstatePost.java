package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Type;

import com.api.bkhouse.constant.enumeric.EDirection;
import com.api.bkhouse.constant.enumeric.EStatus;
import com.api.bkhouse.constant.enumeric.EType;

// Import JTS cho kiểu PostGIS Geography (Cần thư viện hibernate-spatial)
import lombok.*;
import org.locationtech.jts.geom.Point; 

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Entity
@Table(name = "real_estate_posts")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstatePost {

    @Id // Lưu UUID dưới dạng chuỗi trong DB
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull
    private EType type;

    // Quan hệ Nhiều - Một (Một user có thể có nhiều bài đăng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "title", columnDefinition = "TEXT")
    @NotBlank
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank
    private String description;

    @Column(name = "address_show", columnDefinition = "TEXT")
    @NotBlank
    private String addressShow;

    @Column(name = "area")
    @NotNull
    private Double area;

    @Column(name = "price")
    @NotNull
    private Double price;

    @Column(name = "price_per_m2", insertable = false, updatable = false)
    private Double pricePerM2;

    // Quan hệ Nhiều - Một 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_code")
    @NotNull
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_code")
    @NotNull
    private District district;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private EStatus status;

    // Cột vị trí sử dụng kiểu Geography
    @Column(name = "location", columnDefinition = "geography(Point, 4326)")
    private Point location; 

    @Column(name = "is_enabled")
    @NotNull
    private Boolean enable;

    @Column(name = "is_sell")
    @NotNull
    private Boolean isSell;

    @Column(name = "priority")
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private EDirection direction;

    @Column(name = "street", columnDefinition = "TEXT")
    private String street;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "contact_count")
    private Integer contactCount = 0;

    // Lưu trữ metadata linh hoạt dưới dạng JSON
    @Type(type = "jsonb")
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata; 

    // Trường Embedding để hỗ trợ tìm kiếm vector (AI)
    @Column(name = "embedding", columnDefinition = "vector")
    private String embedding; 

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "realEstatePost")
    private List<RealEstatePostPrice> realEstatePostPrices;

    @Transient
    private Integer period;

    // ==========================================
    // GETTERS VÀ SETTERS
    // ==========================================

    // Tự động gán thời gian khi Thêm mới/Cập nhật bản ghi
    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.status == null) this.status = EStatus.PENDING; // Mặc định là PENDING

        if (this.enable == null) this.enable = true; // Mặc định bài viết mới tạo là true
        if (this.isSell == null) this.isSell = true;   // Chống cháy nếu isSell cũng bị ModelMapper lỡ nhịp
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
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
        return owner;
    }

    public void setOwnerId(User owner) {
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getSell() {
        return isSell;
    }

    public void setSell(Boolean sell) {
        isSell = sell;
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
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

    public List<RealEstatePostPrice> getRealEstatePostPrices() {
        return realEstatePostPrices;
    }

    public void setRealEstatePostPrices(List<RealEstatePostPrice> realEstatePostPrices) {
        this.realEstatePostPrices = realEstatePostPrices;
    }
    
    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
}

