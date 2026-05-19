package com.api.bkhouse.payload.response;
import com.api.bkhouse.constant.enumeric.EDirection;


import java.time.Instant;

//import org.springframework.security.access.method.P;


public class RepDetailPageResponse {
    private String id;
    private String avatarUrl;
    private String fullName;
    private Instant createAt;
    private String phoneNumber;
    private boolean interested;
    private String description;
    private String title;
    private Double price;
    private Double area;
    private String addressShow;
    private String imageUrl;
    private EDirection direction;
    private boolean enable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getAddressShow() {
        return addressShow;
    }

    public void setAddressShow(String addressShow) {
        this.addressShow = addressShow;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public EDirection getDirection() {
        return direction;
    }

    public void setDirection(EDirection direction) {
        this.direction = direction;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
