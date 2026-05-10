package com.api.bkhouse.entity.response;

public interface IRepRequested {
    String getId();
    String getType();
    String getTitle();
    
    // Đã sửa: Byte -> Boolean
    Boolean getIsSell(); 
    
    Double getPrice();
    String getStatus();
    String getFullName();
    String getPhoneNumber();
    Long getRepaId();
}