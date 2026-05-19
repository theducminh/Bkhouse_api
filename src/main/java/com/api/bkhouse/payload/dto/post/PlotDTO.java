package com.api.bkhouse.payload.dto.post;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PlotDTO extends BasePost{
    @NotNull
    private Long id;

    @NotNull
    private Double frontWidth;

    @NotNull
    private Double behindWidth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getFrontWidth() {
        return frontWidth;
    }

    public void setFrontWidth(Double frontWidth) {
        this.frontWidth = frontWidth;
    }

    public Double getBehindWidth() {
        return behindWidth;
    }

    public void setBehindWidth(Double behindWidth) {
        this.behindWidth = behindWidth;
    }
}
