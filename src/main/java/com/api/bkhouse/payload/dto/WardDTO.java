package com.api.bkhouse.payload.dto;

public class WardDTO {
    private String code;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private String codeName;
//    private AdministrativeUnitDTO administrativeUnitDTO;
//    private DistrictDTO districtDTO;
    private Integer administrativeUnitId;
    private String districtCode;

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
//    public DistrictDTO getDistrictDTO() {
//        return districtDTO;
//    }
//
//    public void setDistrictDTO(DistrictDTO districtDTO) {
//        this.districtDTO = districtDTO;
//    }

    public Integer getAdministrativeUnitId() {
        return administrativeUnitId;
    }

    public void setAdministrativeUnitId(Integer administrativeUnitId) {
        this.administrativeUnitId = administrativeUnitId;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }
}
