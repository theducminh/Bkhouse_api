package com.api.bkhouse.entity.response;

import java.time.Instant;

public interface IInterestedUser {
    String getId();
    String getPhoneNumber();
    String getEmail();
    String getAvatarUrl();
    String getFullName();
    Instant getCreateAt();
}
