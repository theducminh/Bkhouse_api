package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EType;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "statistic_price_fluctuation")
public class StatisticPriceFluctuation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price")
    private Double price;

    @Column(name = "create_at")
    @NotNull
    @NotBlank
    private Date createAt;

    @Column(name = "sell")
    @NotNull
    @NotBlank
    private boolean sell;

    @Column(name = "type")
    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private EType type;

    @Column(name = "province_code")
    @NotNull
    @NotBlank
    private String provinceCode;

    @Column(name = "ward_code")
    @NotNull
    @NotBlank
    private String wardCode;

    @Column(name = "district_code")
    @NotNull
    @NotBlank
    private String districtCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public EType getType() {
        return type;
    }

    public void setType(EType type) {
        this.type = type;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }
}
