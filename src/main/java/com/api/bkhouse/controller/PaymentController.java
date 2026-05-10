package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.PostPay;
import com.api.bkhouse.entity.SpecialAccountPay;
import com.api.bkhouse.payload.dto.PostPayDTO;
import com.api.bkhouse.payload.dto.SpecialAccountPayDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.PaymentResponse;
import com.api.bkhouse.service.PaymentService;
import com.api.bkhouse.service.PostPayService;
import com.api.bkhouse.service.SpecialAccountPayService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/payment")
public class PaymentController {
    @Autowired
    private PostPayService postPayService;

    @Autowired
    private SpecialAccountPayService specialAccountPayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_ENTERPRISE') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> findAllByUserId(@PathVariable("userId") UUID userId) {
        try {
            List<PostPay> postPays = postPayService.findByUserId(userId);
            List<SpecialAccountPay> specialAccountPays = specialAccountPayService.getSpecialAccountPaysByUserId(userId);
            List<PaymentResponse> paymentResponses = new ArrayList<>();
            for (PostPay postPay: postPays) {
                paymentResponses.add(
                        new PaymentResponse(postPay.getContent(),
                                postPay.getPrice(),
                                postPay.getAccountBalance(),
                                postPay.getCreateAt())
                );
            }
            for (SpecialAccountPay specialAccountPay: specialAccountPays) {
                paymentResponses.add(new PaymentResponse(
                        specialAccountPay.getContent(),
                        specialAccountPay.getAmount(),
                        specialAccountPay.getAccountBalance(),
                        specialAccountPay.getCreateAt()
                ));
            }
            return ResponseEntity.ok(new BaseResponse(
                    paymentResponses.stream()
                            .sorted(Comparator
                                    .comparing(PaymentResponse::getCreateAt)
                                    .reversed())
                            .collect(Collectors.toList()),
                    "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách lịch sử thanh toán của người dùng. "
                    + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/post-pay")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllPostPay() {
        try {
            return ResponseEntity.ok(
                    new BaseResponse(postPayService
                            .findAllPostPays()
                            .stream()
                            .map(e -> modelMapper.map(e, PostPayDTO.class))
                            .collect(Collectors.toList()),
                            "", HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách lịch sử thanh toán của người dùng. "
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/special-account-pay")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllSpecialAccountPay() {
        try {
            return ResponseEntity.ok(
                    new BaseResponse(specialAccountPayService
                            .findAllSpecialAccountPays()
                            .stream()
                            .map(e -> modelMapper.map(e, SpecialAccountPayDTO.class))
                            .collect(Collectors.toList()),
                            "", HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách lịch sử thanh toán của người dùng. "
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/statistic/thanh-toan-nam")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> thanhToanNam(@RequestParam Integer nam) {
        try {
            return ResponseEntity.ok(new BaseResponse(paymentService.getPaymentStatistic(nam), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin thanh toán năm. " + e.getMessage()
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/statistic/thanh-toan-thang")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> thanhToanThang(@RequestParam Integer nam, @RequestParam Integer thang) {
        try {
            return ResponseEntity.ok(new BaseResponse(paymentService.getPaymentStatisticMonth(nam, thang), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin thanh toán theo tháng. " + e.getMessage()
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/statistic/nap-tien-nam")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> napTienNam(@RequestParam Integer nam) {
        try {
            return ResponseEntity.ok(new BaseResponse(paymentService.getChargeInYear(nam), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin nạp tiền trong năm. " + e.getMessage()
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/statistic/nap-tien-thang")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> napTienThang(@RequestParam Integer nam, @RequestParam Integer thang) {
        try {
            return ResponseEntity.ok(new BaseResponse(paymentService.getChargeByMonth(nam, thang), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin nạp tiền trong năm. " + e.getMessage()
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
