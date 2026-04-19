package com.api.bkhouse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.NotifyService;
import com.api.bkhouse.service.RealEstatePostService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RequestMapping("/api/v1/schedule")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ScheduleController {
    @Autowired
    private RealEstatePostService realEstatePostService;

    @Autowired
    private NotifyService notifyService;

    @PostMapping("/disable-post-expire")
    public ResponseEntity<BaseResponse> disablePostExpire() {
        realEstatePostService.disablePostExpire();
        return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
    }

    @PostMapping("/new-enterprise-pay")
    public ResponseEntity<BaseResponse> thongBaoChuaDongTien() {
        notifyService.thongBaoChuaDongTien();
        return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
    }

    @PostMapping("/special-account-pay")
    public ResponseEntity<BaseResponse> thongBaoDenHanDongTien() {
        notifyService.thongBaoDenHanDongTien();
        return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
    }

    @PostMapping("/statistic")
    public ResponseEntity<BaseResponse> statisticPricePerAreaUnit() {
        Date currDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = simpleDateFormat.format(currDate);
        realEstatePostService.calculatePricePerAreaUnit(dateFormat);
        return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
    }
}
