package com.api.bkhouse.entity.response;

import java.time.Instant;
import java.util.UUID;
public interface IRepAdmin {
    String getId();
    String getType();
    String getFullName();
    
    // Đã sửa: Byte -> Boolean
    Boolean getSell(); 
    
    String getPhoneNumber();
    String getStatus();
    
    // Đã sửa: Byte -> Boolean
    Boolean getEnable(); 
    
    Double getPrice();
    Double getArea();
    Instant getCreateAt();
    String getImageUrl();
    String getTitle();
    String getAddressShow();
}