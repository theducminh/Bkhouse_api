package com.api.bkhouse.service;

import com.api.bkhouse.constant.Message;
import com.api.bkhouse.constant.enumeric.ERepAgencyStatus;
import com.api.bkhouse.repository.UserDeviceTokenRepository;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.UUID;
@Service
public class NotifyService {
    @Value("${app.domain}")
    private String domain;

    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FirebaseMessaging fcm;

    private Logger logger = LoggerFactory.getLogger(NotifyService.class);

    public void notifyToAllUsers(String message) {
        logger.info("domain: {}", domain);
        sendNotify(userDeviceTokenRepository.getAllAdminToken(), message, domain);
    }

    public void notifyPriceFluctuation(String message, List<String> districtCodes, Long postId) {
        Set<String> tokenSet = new HashSet<>();
        for (String districtCode: districtCodes) {
            List<String> tokens = userDeviceTokenRepository.getTokensByDistrict(districtCode);
            tokenSet.addAll(tokens);
        }
        sendNotify(tokenSet.stream().toList(), message, domain + "tien-ich/tin-tuc/detail/" + postId);
    }

    public void notifyAgencyREPUpdate(String message, String districtCode, UUID postId, boolean isUpdate) {
        List<String> tokens = userDeviceTokenRepository.notifyAgencyREPUpdate(districtCode);
        sendNotify(tokens, message, isUpdate ? domain + "user/cooperate-agency/" + postId : domain + "home/" + postId);
    }

    public void notifyAcceptRejectREP(String message, UUID userId, UUID postId, boolean isAccept) {
        List<String> tokens = userDeviceTokenRepository.notifyAcceptRejectREP(userId);
        sendNotify(tokens, message, isAccept ? domain + "home/" + postId : domain + "user/post/main/" + postId);
    }

    public void notifyInterested(String message, UUID postId) {
        List<String> tokens = userDeviceTokenRepository.notifyInterested(postId);
        sendNotify(tokens, message, domain + "user/focus/" + postId);
    }

    public void notifyToAdmin(String message, UUID postId) {
        List<String> tokens = userDeviceTokenRepository.getAllAdminToken();
        sendNotify(tokens, message, domain + "admin/post/main/" + postId);
    }

    public void thongBaoChuaDongTien() {
        List<String> tokens = userDeviceTokenRepository.thongBaoChuaDongTien();
        sendNotify(tokens, Message.THONG_BAO_CHUA_DONG_TIEN, domain + "user/balance/fluctuation");
    }

    public void thongBaoDenHanDongTien() {
        List<String> tokens = userDeviceTokenRepository.thongBaoDenHanDongTien();
        sendNotify(tokens, Message.THONG_BAO_DEN_HAN_DONG_TIEN, domain + "user/balance/fluctuation");
    }

    public void thongBaoCoBaiDangNhoGiup(UUID agencyId, UUID postId) {
        List<String> tokens = userDeviceTokenRepository.thongBaoCoBaiDangNhoGiup(agencyId);
        sendNotify(tokens, Message.CO_BAI_DANG_MOI_NHO_GIUP, domain + "user/cooperate-agency/" + postId);
    }

    public void thongBaoCapNhatTrangThaiBaiDang( UUID realEstatePostId, ERepAgencyStatus status, UUID postId) {
        String message;
        if (status.equals(ERepAgencyStatus.DA_TU_CHOI)) {
            message = "Bài viết bạn nhờ môi giới giúp đỡ đã bị từ chối";
        } else {
            message = "Bài viết bạn nhờ môi giới giúp đỡ đã được chấp nhận";
        }
        List<String> tokens = userDeviceTokenRepository.thongBaoCapNhatTrangThaiBaiDang(realEstatePostId);
        sendNotify(tokens, message, domain + "user/post/main/" + postId);
    }

    public void thongBaoHoanThanhBaiDang(String message, UUID realEstatePostId) {
        List<String> tokens = userDeviceTokenRepository.thongBaoHoanThanhBaiDang(realEstatePostId);
        sendNotify(tokens, message, domain);
    }

    private void sendNotify(List<String> tokens, String message, String link) {
        try {
            for (String token : tokens) {
                logger.info("token: {}", token);
                WebpushConfig webpushConfig = WebpushConfig.builder()
                        .setNotification(
                                WebpushNotification.builder()
                                        .setBody(message).build())
                        .setFcmOptions(
                                WebpushFcmOptions.builder()
                                        .setLink(link).build())
                        .build();

                com.google.firebase.messaging.Message msg =
                        com.google.firebase.messaging.Message.builder()
                                .setToken(token)
                                .setWebpushConfig(webpushConfig)
                                .setNotification(
                                        Notification.builder()
                                                .setTitle("Bkland")
                                                .setBody(message)
                                                .build())
                                .build();
                fcm.send(msg);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
