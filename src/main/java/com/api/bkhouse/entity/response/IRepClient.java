package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IRepClient {
    String getId();
    String getTitle();
    String getAddressShow();
    Double getPrice();
    Double getArea();
    
    // ĐÃ SỬA Ở ĐÂY: Chuyển từ Byte sang Boolean
    Boolean getSell(); 
    
    Instant getCreateAt();
    String getImageUrl();
}