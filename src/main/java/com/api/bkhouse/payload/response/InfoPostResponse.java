package com.api.bkhouse.payload.response;

import com.api.bkhouse.payload.dto.InfoPostDTO;
import com.api.bkhouse.payload.dto.UserDTO;

public class InfoPostResponse extends InfoPostDTO {
    private UserDTO user;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
