package com.api.bkhouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "provinces")
public class Province {
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
//
//    @JoinColumn(name = "administrative_region_id")
//    @ManyToOne
//    @JoinTable(name = "administrative_regions",
//            joinColumns = @JoinColumn(name = "id"))
//    private AdministrativeRegion administrativeRegion;

    @Column(name = "administrative_unit_id")
    private String administrativeUnitId;

    @Column(name = "administrative_region_id")
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
