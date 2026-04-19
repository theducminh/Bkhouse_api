package com.api.bkhouse.payload.response;

public class MediaResponse {
    private String id;
    private String type;
    private String body;

    public MediaResponse(String id, String type, String body) {
        this.id = id;
        this.type = type;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
