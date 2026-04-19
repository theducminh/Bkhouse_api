package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IInfoPost {
    Long getId();
    String getTitle();
    String getImageUrl();
    String getFullName();
    Instant getCreateAt();
}
