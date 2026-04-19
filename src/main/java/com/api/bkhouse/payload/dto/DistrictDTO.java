package com.api.bkhouse.payload.dto;


public class DistrictDTO {
    private String code;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private String codeName;
//    private AdministrativeUnitDTO administrativeUnitDTO;
//    private ProvinceDTO provinceDTO;
    private Integer administrativeUnitId;
    private String provinceCode;

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

//    public AdministrativeUnitDTO getAdministrativeUnitDTO() {
//        return administrativeUnitDTO;
//    }
//
//    public void setAdministrativeUnitDTO(AdministrativeUnitDTO administrativeUnitDTO) {
//        this.administrativeUnitDTO = administrativeUnitDTO;
//    }
//
//    public ProvinceDTO getProvinceDTO() {
//        return provinceDTO;
//    }
//
//    public void setProvinceDTO(ProvinceDTO provinceDTO) {
//        this.provinceDTO = provinceDTO;
//    }

    public Integer getAdministrativeUnitId() {
        return administrativeUnitId;
    }

    public void setAdministrativeUnitId(Integer administrativeUnitId) {
        this.administrativeUnitId = administrativeUnitId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
