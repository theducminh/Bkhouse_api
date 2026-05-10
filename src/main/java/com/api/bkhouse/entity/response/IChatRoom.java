package com.api.bkhouse.entity.response;
import java.util.UUID;

public interface IChatRoom {
    Integer getId();
    String getAvatarUrl();
    String getMessage();
    String getCreateBy();
    String getFullName();
}
