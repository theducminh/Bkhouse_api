package com.api.bkhouse.payload.response.chart;

import java.util.ArrayList;
import java.util.List;

public class Series {
    private String name;
    private List<Object> data;

    public Series() {
        data = new ArrayList<>();
    }

    public Series(String name, List<Object> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
