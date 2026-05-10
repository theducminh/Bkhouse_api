package com.api.bkhouse.entity.response;

public interface IPostInterested {
    String getId();
    String getType();
    String getTitle();
    String getArea();
    
    // Đã sửa: Byte -> Boolean
    Boolean getIsSell(); 
    
    Double getPrice();
    String getAddressShow();
}