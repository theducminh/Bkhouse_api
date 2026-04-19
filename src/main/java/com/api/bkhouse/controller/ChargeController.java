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
    private String vnp_OrderInfo;
    @Value(value = "${vnp_OrderType}")
    private String vnp_OrderType;
    @Value(value = "${vnp_BankCode}")
    private String vnp_BankCode;
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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> createCharge(@RequestBody ChargeDTO chargeDTO) {
        try {
            chargeDTO.setCreateAt(Util.getCurrentDateTime());
            if (chargeDTO.getChargeType().equals(EChargeType.TRANSFER_CHARGE)) {
                chargeDTO.setStatus(EChargeStatus.CHO_XAC_NHAN);
            } else {
                chargeDTO.setStatus(EChargeStatus.DA_XAC_NHAN);
            }
            Charge charge = chargeService.insert(modelMapper.map(chargeDTO, Charge.class));
            if (chargeDTO.getChargeType().equals(EChargeType.VNPAY)) {
                User user = charge.getUser();
                user.setAccountBalance(user.getAccountBalance() + chargeDTO.getSoTien());
                user.setUpdatedAt(Util.getCurrentDateTime());
                userService.updateUserInfo(user);
                charge.setAccountBalance(user.getAccountBalance());
                chargeService.update(charge);
            }
            return ResponseEntity.ok(
                    new BaseResponse(null,
                            "Nạp tiền thành công. "
                            + (chargeDTO.getStatus().equals(EChargeStatus.CHO_XAC_NHAN) ? "Chờ quản trị viên xác nhận." : ""),
                            HttpStatus.OK)
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
                user.setAccountBalance(user.getAccountBalance() + chargeDTO.getSoTien());
                user.setUpdatedAt(Util.getCurrentDateTime());
                userService.updateUserInfo(user);
            }
            return ResponseEntity.ok(
                    new BaseResponse(null,
                            "Cập nhật thông tin nạp tiền thành công.",
                            HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật thông tin nạp tiền. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_AGENCY') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getChargeByUserId(@PathVariable("userId") String userId) {
        try {
            return ResponseEntity.ok(
                    new BaseResponse(
                            chargeService.findByUserId(userId)
                                    .stream().map(e -> convertToDTO(e)).collect(Collectors.toList()),
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
                    new BaseResponse(
                            chargeService.findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Charge::getCreateAt).reversed())
                                    .map(e -> convertToDTO(e)).collect(Collectors.toList()),
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

    @GetMapping("/vnpay-url/{ipAddress}/{userId}/{amount}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> pay(@PathVariable("ipAddress") String vnp_IpAddr,
                      @PathVariable("userId") String userId,
                      @PathVariable("amount") Long amount)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String vnp_TxnRef = Util.getRandom8Number();
        String vnp_Amount = String.valueOf(amount*100);

        vnp_OrderInfo = userId;

        Map vnp_Params = new HashMap();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
//        vnp_Params.put("vnp_BankCode", vnp_BankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.of("UTC+07:00")).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = localDateTime.format(dateTimeFormatter);

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        LocalDateTime localDateTimeExpire = localDateTime.plusMinutes(15L);
        String vnp_ExpireDate = localDateTimeExpire.format(dateTimeFormatter);
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        // Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
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
}
