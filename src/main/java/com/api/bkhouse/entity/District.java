package com.api.bkhouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "districts")
public class District {
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

//    @JoinColumn(name = "province_code")
//    @ManyToOne
//    @JoinTable(name = "provinces",
//            joinColumns = @JoinColumn(name = "code"))
//    private Province province;

    @Column(name = "administrative_unit_id")
    private String administrativeUnitId;

    @Column(name = "province_code")
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

//    public AdministrativeUnit getAdministrativeUnit() {
//        return administrativeUnit;
//    }
//
//    public void setAdministrativeUnit(AdministrativeUnit administrativeUnit) {
//        this.administrativeUnit = administrativeUnit;
//    }

    public String getAdministrativeUnitId() {
        return administrativeUnitId;
    }

    public void setAdministrativeUnitId(String administrativeUnitId) {
        this.administrativeUnitId = administrativeUnitId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    //    public Province getProvince() {
//        return province;
//    }
//
//    public void setProvince(Province province) {
//        this.province = province;
//    }
}
