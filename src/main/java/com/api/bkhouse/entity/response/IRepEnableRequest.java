package com.api.bkhouse.entity.response;

public interface IRepEnableRequest {
    String getId();
    String getType();
    String getTitle();
    String getDistrictCode();
    Byte getIsSell();
    Double getPrice();
}
