package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IRepAdmin {
    String getId();
    String getType();
    String getFullName();
    Byte getSell();
    String getPhoneNumber();
    String getStatus();
    Byte getEnable();
    Double getPrice();
    Double getArea();
    Instant getCreateAt();
    String getImageUrl();
    String getTitle();
    String getAddressShow();
}
