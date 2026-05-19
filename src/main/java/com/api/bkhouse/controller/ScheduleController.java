package com.api.bkhouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDate;

@RequestMapping("/api/v1/schedule")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ScheduleController {
    private final RealEstatePostService realEstatePostService;

    private final NotifyService notifyService;

    public ScheduleController(RealEstatePostService realEstatePostService, NotifyService notifyService) {
        this.realEstatePostService = realEstatePostService;
        this.notifyService = notifyService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @PostMapping("/disable-post-expire")
    public ResponseEntity<BaseResponse> disablePostExpire() {
        try {
            realEstatePostService.disablePostExpire();
            return ResponseEntity.ok(new BaseResponse(null, "Quét bài viết hết hạn thành công", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi chạy schedule quét bài viết hết hạn: ", e);
            return ResponseEntity.ok(new BaseResponse(null, 
                    "Lỗi khi quét bài viết hết hạn: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/new-enterprise-pay")
    public ResponseEntity<BaseResponse> thongBaoChuaDongTien() {
        try {
            notifyService.thongBaoChuaDongTien();
            return ResponseEntity.ok(new BaseResponse(null, "Gửi thông báo doanh nghiệp chưa đóng tiền thành công", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi chạy schedule thông báo doanh nghiệp chưa đóng tiền: ", e);
            return ResponseEntity.ok(new BaseResponse(null, 
                    "Lỗi khi gửi thông báo doanh nghiệp: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

   @PostMapping("/special-account-pay")
    public ResponseEntity<BaseResponse> thongBaoDenHanDongTien() {
        try {
            notifyService.thongBaoDenHanDongTien();
            return ResponseEntity.ok(new BaseResponse(null, "Gửi thông báo môi giới đến hạn đóng tiền thành công", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi chạy schedule thông báo môi giới đến hạn: ", e);
            return ResponseEntity.ok(new BaseResponse(null, 
                    "Lỗi khi gửi thông báo môi giới: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

   @PostMapping("/statistic")
    public ResponseEntity<BaseResponse> statisticPricePerAreaUnit() {
        try {
            String dateFormat = LocalDate.now().toString(); // Tự động format thành yyyy-MM-dd
            
            realEstatePostService.calculatePricePerAreaUnit(dateFormat);
            return ResponseEntity.ok(new BaseResponse(null, "Chạy thống kê biến động giá thành công", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi chạy schedule thống kê biến động giá: ", e);
            return ResponseEntity.ok(new BaseResponse(null, 
                    "Lỗi khi tính toán thống kê biến động giá: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
