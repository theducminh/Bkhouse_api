package com.api.bkhouse.entity.response;

public interface IRepEnableRequest {
    String getId();
    String getType();
    String getTitle();
    String getDistrictCode();
    
    // Đã sửa: Byte -> Boolean
    Boolean getIsSell(); 
    
    Double getPrice();
}