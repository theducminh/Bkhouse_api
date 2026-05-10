package com.api.bkhouse.payload.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import org.checkerframework.checker.units.qual.N;

import com.api.bkhouse.constant.enumeric.EProjectType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Data
public class ProjectDTO {
    private UUID id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private ProvinceDTO province;
    @NotNull
    @NotBlank
    private DistrictDTO district;
    @NotNull
    @NotBlank
    private WardDTO ward;
    @NotNull
    @NotBlank
    private String address; 
    private Double area;
    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private EProjectType type;
    @NotNull
    @NotBlank
    private String phoneNumber;
    
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String imageUrl;
    @NotNull
    @NotBlank
    private boolean enable;
    @NotBlank
    @NotNull
    
    @NotBlank
    @NotNull
    private Map<String, Object> metadata;
    private Instant createAt;
    private Instant updateAt;
    private UUID createBy;
    private List<ProjectParamDTO> projectParams;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public EProjectType getType() {
        return type;
    }

    public void setType(EProjectType type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

   
    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public UUID getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UUID createBy) {
        this.createBy = createBy;
    }


    public List<ProjectParamDTO> getProjectParams() {
        return projectParams;
    }

    public void setProjectParams(List<ProjectParamDTO> projectParams) {
        this.projectParams = projectParams;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

}
