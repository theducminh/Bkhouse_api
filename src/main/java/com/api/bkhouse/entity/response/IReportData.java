package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IReportData {
    String getPostId();
    String getTitle();
    String getPostType();
    String getFullName();
    String getPhoneNumber();
    Instant getCreateAt();
    Integer getCount();
}
