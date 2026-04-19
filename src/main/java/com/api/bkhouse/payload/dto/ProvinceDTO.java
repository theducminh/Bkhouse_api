package com.api.bkhouse.payload.dto;

public class ProvinceDTO {
    private String code;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private String codeName;
    private String administrativeUnitId;
    private String administrativeRegionId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullNameEn() {
        return fullNameEn;
    }

    public void setFullNameEn(String fullNameEn) {
        this.fullNameEn = fullNameEn;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getAdministrativeUnitId() {
        return administrativeUnitId;
    }

    public void setAdministrativeUnitId(String administrativeUnitId) {
        this.administrativeUnitId = administrativeUnitId;
    }

    public String getAdministrativeRegionId() {
        return administrativeRegionId;
    }

    public void setAdministrativeRegionId(String administrativeRegionId) {
        this.administrativeRegionId = administrativeRegionId;
    }
}
