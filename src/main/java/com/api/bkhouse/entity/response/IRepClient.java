package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IRepClient {
    String getId();
    String getTitle();
    String getAddressShow();
    Double getPrice();
    Double getArea();
    Byte getSell();
    Instant getCreateAt();
    String getImageUrl();
}
