package com.api.bkhouse.payload.response;

import java.util.ArrayList;
import java.util.List;

public class PaymentStatisticResponse {
    private List<Integer> ngay;
    private List<Integer> month;
    private List<Long> postPrice;
    private List<Long> specialAccountPay;

    public PaymentStatisticResponse() {
        this.ngay = new ArrayList<>();
        this.month = new ArrayList<>();
        this.postPrice = new ArrayList<>();
        this.specialAccountPay = new ArrayList<>();
    }

    public List<Integer> getNgay() {
        return ngay;
    }

    public void setNgay(List<Integer> ngay) {
        this.ngay = ngay;
    }

    public List<Integer> getMonth() {
        return month;
    }

    public void setMonth(List<Integer> month) {
        this.month = month;
    }

    public List<Long> getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(List<Long> postPrice) {
        this.postPrice = postPrice;
    }

    public List<Long> getSpecialAccountPay() {
        return specialAccountPay;
    }

    public void setSpecialAccountPay(List<Long> specialAccountPay) {
        this.specialAccountPay = specialAccountPay;
    }
}
