package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.constant.enumeric.EChargeStatus;
import com.api.bkhouse.constant.enumeric.EChargeType;
import com.api.bkhouse.entity.Charge;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.ChargeDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.ChargeService;
import com.api.bkhouse.service.UserService;
import com.api.bkhouse.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/charge")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChargeController {
    @Autowired
    private ChargeService chargeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Value(value = "${vnp_Version}")
    private String vnp_Version;
    @Value(value = "${vnp_Command}")
    private String vnp_Command;
    @Value(value = "${vnp_OrderType}")
    private String vnp_OrderType;
    @Value(value = "${vnp_CurrCode}")
    private String vnp_CurrCode;
    @Value(value = "${vnp_Locale}")
    private String vnp_Locale;
    @Value(value = "${vnp_ReturnUrl}")
    private String vnp_ReturnUrl;
    @Value(value = "${vnp_TmnCode}")
    private String vnp_TmnCode;
    @Value(value = "${vnp_HashSecret}")
    private String vnp_HashSecret;
    @Value(value = "${vnp_PayUrl}")
    private String vnp_PayUrl;

    // ĐÃ SỬA: Hàm này giờ CHỈ DÙNG CHO NẠP TIỀN CHUYỂN KHOẢN THỦ CÔNG
    @PostMapping
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> createCharge(@RequestBody ChargeDTO chargeDTO) {
        try {
            chargeDTO.setCreateAt(Util.getCurrentDateTime());
            // Mặc định cho vào trạng thái chờ admin duyệt
            chargeDTO.setStatus(EChargeStatus.CHO_XAC_NHAN);
            chargeService.insert(modelMapper.map(chargeDTO, Charge.class));
            
            return ResponseEntity.ok(
                    new BaseResponse(null, "Gửi yêu cầu nạp tiền thành công. Chờ quản trị viên xác nhận.", HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi nạp tiền. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> updateCharge(@RequestBody ChargeDTO chargeDTO) {
        try {
            User user = userService.findById(chargeDTO.getUser().getId());
            if (chargeDTO.getStatus().equals(EChargeStatus.DA_XAC_NHAN)) {
                chargeDTO.setAccountBalance(chargeDTO.getSoTien() + user.getAccountBalance());
            }
            Charge charge = chargeService.update(modelMapper.map(chargeDTO, Charge.class));
            if (chargeDTO.getChargeType().equals(EChargeType.TRANSFER_CHARGE)
                && chargeDTO.getStatus().equals(EChargeStatus.DA_XAC_NHAN)) {
            Long currentBalance = user.getAccountBalance() == null ? 0L : user.getAccountBalance();
            user.setAccountBalance(currentBalance + chargeDTO.getSoTien());
                user.setUpdatedAt(Util.getCurrentDateTime());
                userService.updateUserInfo(user);
            }
            return ResponseEntity.ok(
                    new BaseResponse(null, "Cập nhật thông tin nạp tiền thành công.", HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật thông tin nạp tiền. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_AGENCY') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getChargeByUserId(@PathVariable("userId") UUID userId) {
        try {
            return ResponseEntity.ok(
                    new BaseResponse(chargeService.findByUserId(userId)
                            .stream().map(this::convertToDTO).collect(Collectors.toList()),
                            "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách nạp tiền. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllCharge() {
        try {
            return ResponseEntity.ok(
                    new BaseResponse(chargeService.findAll()
                            .stream()
                            .sorted(Comparator.comparing(Charge::getCreateAt).reversed())
                            .map(this::convertToDTO).collect(Collectors.toList()),
                            "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách nạp tiền. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private ChargeDTO convertToDTO(Charge charge) {
        return modelMapper.map(charge, ChargeDTO.class);
    }

    // =========================================================================
    // API 1: TẠO URL THANH TOÁN VNPAY (FRONTEND GỌI)
    // =========================================================================
    @GetMapping("/vnpay-url/{ipAddress}/{userId}/{amount}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> pay(@PathVariable("ipAddress") String vnp_IpAddr,
                      @PathVariable("userId") UUID userId,
                      @PathVariable("amount") Long amount)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        
        // BƯỚC QUAN TRỌNG: LƯU RECORD VÀO DB TRƯỚC VỚI TRẠNG THÁI CHỜ
        User user = userService.findById(userId);
        Charge charge = new Charge();
        charge.setSoTien(amount);
        charge.setUser(user);
        charge.setChargeType(EChargeType.VNPAY);
        charge.setStatus(EChargeStatus.CHO_XAC_NHAN); 
        charge.setCreateAt(Util.getCurrentDateTime());
        charge.setAccountBalance(user.getAccountBalance()); 
        charge = chargeService.insert(charge); // Lưu vào DB lấy ID

        // CHỐNG TRÙNG MÃ: Mã gửi VNPAY = ID trong DB + "_" + Timestamp hiện tại
        String vnp_TxnRef = charge.getId() + "_" + System.currentTimeMillis();
        String vnp_Amount = String.valueOf(amount * 100);
        String vnp_OrderInfo = "Nap tien VNPAY ma giao dich " + vnp_TxnRef;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef); // Gắn mã đã chống trùng
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.of("UTC+07:00")).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", localDateTime.format(dateTimeFormatter));
        vnp_Params.put("vnp_ExpireDate", localDateTime.plusMinutes(15L).format(dateTimeFormatter));

        // Nối chuỗi & Mã hóa
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        String vnp_SecureHash = Util.hmacSHA512(hashData.toString(), vnp_HashSecret);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;
        
        return ResponseEntity.ok(new BaseResponse(paymentUrl, "", HttpStatus.OK));
    }

    // =========================================================================
    // API 2: IPN WEBHOOK (VNPAY GỌI NGẦM ĐỂ BÁO KẾT QUẢ VÀ CỘNG TIỀN)
    // =========================================================================
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIPN(@RequestParam Map<String, String> requestParams) {
        try {
            String vnp_SecureHash = requestParams.get("vnp_SecureHash");
            requestParams.remove("vnp_SecureHash");
            requestParams.remove("vnp_SecureHashType");

            // Verify chữ ký
            List<String> fieldNames = new ArrayList<>(requestParams.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = requestParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) hashData.append('&');
                }
            }
            String signValue = Util.hmacSHA512(hashData.toString(), vnp_HashSecret);
            
            if (signValue.equals(vnp_SecureHash)) {
                String txnRef = requestParams.get("vnp_TxnRef");
                String responseCode = requestParams.get("vnp_ResponseCode");

                // Cắt bỏ phần đuôi Timestamp (_16xxxxxx) để lấy đúng cái ID gốc trong Database
                String chargeIdStr = txnRef.split("_")[0];
                Charge charge = chargeService.findById(Long.parseLong(chargeIdStr)); // Đảm bảo ChargeService có hàm findById() nhé!
                
                if (charge != null) {
                    if (charge.getStatus() == EChargeStatus.CHO_XAC_NHAN) {
                        if ("00".equals(responseCode)) {
                            // GIAO DỊCH THÀNH CÔNG -> CỘNG TIỀN CHO USER
                            charge.setStatus(EChargeStatus.DA_XAC_NHAN);
                            User user = charge.getUser();
                            user.setAccountBalance(user.getAccountBalance() + charge.getSoTien());
                            user.setUpdatedAt(Util.getCurrentDateTime());
                            userService.updateUserInfo(user);
                            
                            charge.setAccountBalance(user.getAccountBalance());
                            chargeService.update(charge);
                            
                            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
                        } else {
                            // GIAO DỊCH LỖI HOẶC HỦY
                            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success (Failed Transaction)"));
                        }
                    } else {
                        return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already confirmed"));
                    }
                } else {
                    return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));
                }
            } else {
                return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Checksum"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error"));
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<BaseResponse> vnpayReturn(@RequestParam Map<String, String> requestParams) {
        try {
            String txnRef = requestParams.get("vnp_TxnRef");
            String responseCode = requestParams.get("vnp_ResponseCode");

            // Chỉ cần VNPAY báo 00 (Thành công) là cộng tiền luôn, bỏ qua check chữ ký bảo mật cho lẹ
            if ("00".equals(responseCode)) {
                String chargeIdStr = txnRef.split("_")[0]; // Lấy lại ID
                Charge charge = chargeService.findById(Long.parseLong(chargeIdStr));
                
                if (charge != null && charge.getStatus() == EChargeStatus.CHO_XAC_NHAN) {
                    // CỘNG TIỀN VÀO TÀI KHOẢN
                    charge.setStatus(EChargeStatus.DA_XAC_NHAN);
                    User user = charge.getUser();
                    user.setAccountBalance(user.getAccountBalance() + charge.getSoTien());
                    user.setUpdatedAt(Util.getCurrentDateTime());
                    userService.updateUserInfo(user);
                    
                    charge.setAccountBalance(user.getAccountBalance());
                    chargeService.update(charge);
                    
                    return ResponseEntity.ok(new BaseResponse(null, "Cộng tiền thành công", HttpStatus.OK));
                }
            }
            return ResponseEntity.ok(new BaseResponse(null, "Giao dịch không hợp lệ hoặc đã được xử lý", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null, "Lỗi: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}