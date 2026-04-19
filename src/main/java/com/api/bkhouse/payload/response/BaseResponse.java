package com.api.bkhouse.payload.response;

import org.springframework.http.HttpStatus;

public class BaseResponse {
    private Object data;
    private String message;
    private int status;

    public BaseResponse(Object data, String message, HttpStatus state) {
        this.data = data;
        this.message = message;
        this.status = state.value();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }
}
