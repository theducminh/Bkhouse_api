package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IRepClientAdministration {
    String getId();
    String getTitle();
    Byte getSell();
    String getType();
    Double getPrice();
    String getDescription();
    Byte getEnable();
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
