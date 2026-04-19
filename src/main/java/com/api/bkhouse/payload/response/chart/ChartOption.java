package com.api.bkhouse.payload.response.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartOption {
    private List<Object> xaxis;
    private List<Object> series;

    public ChartOption() {
        this.xaxis = new ArrayList<>();
        this.series = new ArrayList<>();
    }

    public List<Object> getXaxis() {
        return xaxis;
    }

    public void setXaxis(List<Object> xaxis) {
        this.xaxis = xaxis;
    }

    public List<Object> getSeries() {
        return series;
    }

    public void setSeries(List<Object> series) {
        this.series = series;
    }
}
