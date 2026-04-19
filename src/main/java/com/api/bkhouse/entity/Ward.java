package com.api.bkhouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "wards")
public class Ward {
    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "full_name_en")
    private String fullNameEn;

    @Column(name = "code_name")
    private String codeName;

//    @JoinColumn(name = "administrative_unit_id")
//    @ManyToOne
//    @JoinTable(name = "administrative_units",
//            joinColumns = @JoinColumn(name = "id"))
//    private AdministrativeUnit administrativeUnit;

//    @JoinColumn(name = "district_code")
//    @ManyToOne
//    @JoinTable(name = "districts",
//            joinColumns = @JoinColumn(name = "code"))
//    private District district;

    @Column(name = "administrative_unit_id")
    private Integer administrativeUnitId;

    @Column(name = "district_code")
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

//    public AdministrativeUnit getAdministrativeUnit() {
//        return administrativeUnit;
//    }
//
//    public void setAdministrativeUnit(AdministrativeUnit administrativeUnit) {
//        this.administrativeUnit = administrativeUnit;
//    }

//    public District getDistrict() {
//        return district;
//    }
//
//    public void setDistrict(District district) {
//        this.district = district;
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
