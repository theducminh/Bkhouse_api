package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.response.IPaymentStatistic;
import com.api.bkhouse.payload.response.PaymentStatisticResponse;
import com.api.bkhouse.repository.PostPayRepository;
import com.api.bkhouse.util.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class PaymentService {
    
    private final PostPayRepository postPayRepository;

    public PaymentService(PostPayRepository postPayRepository) {
        this.postPayRepository = postPayRepository;
    }

    public PaymentStatisticResponse getPaymentStatistic(Integer year) {
        int currMonth = Util.getCurrMonth(year);
        PaymentStatisticResponse response = new PaymentStatisticResponse();

        for (int i = 1; i <= currMonth; i++) {
            response.getMonth().add(i);
            Optional<IPaymentStatistic> iPaymentStatistic = postPayRepository.getPaymentStatistic(i, year);
            if (iPaymentStatistic.isPresent()) {
                IPaymentStatistic stat = iPaymentStatistic.get();
                response.getPostPrice().add(stat.getPostPrice() != null ? stat.getPostPrice() : 0L);
                response.getSpecialAccountPay().add(stat.getSpecialAccountPay() != null ? stat.getSpecialAccountPay() : 0L);
            } else {
                // Nếu không có data, thêm 0 để biểu đồ không bị gãy nhịp
                response.getPostPrice().add(0L);
                response.getSpecialAccountPay().add(0L);
            }
        }
        return response;
    }

    public PaymentStatisticResponse getPaymentStatisticMonth(Integer year, Integer month) {
        int date = Util.getDayOfMonth(month, year);
        PaymentStatisticResponse response = new PaymentStatisticResponse();
        for (int i = 1; i <= date; i++) {
            response.getNgay().add(i);
            response.getMonth().add(month);
            String dateReq = String.format("%04d-%02d-%02d", year, month, i);
            Optional<IPaymentStatistic> iPaymentStatistic = postPayRepository.getPaymentStatisticMonth(dateReq);
            if (iPaymentStatistic.isPresent()) {
                IPaymentStatistic stat = iPaymentStatistic.get();
                response.getPostPrice().add(stat.getPostPrice() != null ? stat.getPostPrice() : 0L);
                response.getSpecialAccountPay().add(stat.getSpecialAccountPay() != null ? stat.getSpecialAccountPay() : 0L);
            } else {
                // Nếu không có data, thêm 0 để biểu đồ không bị gãy nhịp
                response.getPostPrice().add(0L);
                response.getSpecialAccountPay().add(0L);
            }
        }
        return response;
    }

    public PaymentStatisticResponse getChargeInYear(Integer nam) {
        int currMonth = Util.getCurrMonth(nam);
        PaymentStatisticResponse response = new PaymentStatisticResponse();

        for (int i = 1; i <= currMonth; i++) {
            response.getMonth().add(i);
            Long iPaymentStatistic = postPayRepository.getChargeInYear(i, nam);

            response.getPostPrice().add(iPaymentStatistic != null ? iPaymentStatistic : 0L);
        }
        return response;
    }

    public PaymentStatisticResponse getChargeByMonth(Integer year, Integer month) {
        int date = Util.getDayOfMonth(month, year);
        PaymentStatisticResponse response = new PaymentStatisticResponse();

        for (int i = 1; i <= date; i++) {
            response.getNgay().add(i);
            response.getMonth().add(month);
            String dateReq = String.format("%04d-%02d-%02d", year, month, i);
            Long iPaymentStatistic = postPayRepository.getChargeInMonth(dateReq);
            response.getPostPrice().add(iPaymentStatistic != null ? iPaymentStatistic : 0L);
        }
        return response;
    }
}
