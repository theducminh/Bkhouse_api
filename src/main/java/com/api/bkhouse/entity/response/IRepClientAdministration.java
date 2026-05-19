package com.api.bkhouse.entity.response;

import java.time.Instant;
import java.util.UUID;
public interface IRepClientAdministration {
    String getId();
    String getTitle();
    
    // Đã sửa: Byte -> Boolean
    Boolean getSell(); 
    
    String getType();
    Double getPrice();
    String getDescription();
    
    // Đã sửa: Byte -> Boolean
    Boolean getEnable(); 
    
    String getStatus();
    Instant getCreateAt();
    Double getArea();
    Instant getUpdateAt();
    Long getClickedView();
    Long getView();
    Long getComment();
    Long getReport();
    Long getInterested();
}