package com.api.bkhouse.service;

import com.api.bkhouse.constant.Message;
import com.api.bkhouse.constant.enumeric.ERepAgencyStatus;
import com.api.bkhouse.repository.UserDeviceTokenRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@Service
public class NotifyService {
    @Value("${app.domain}")
    private String domain;

    private final UserDeviceTokenRepository userDeviceTokenRepository;


    private final FirebaseMessaging fcm;

    public NotifyService(UserDeviceTokenRepository userDeviceTokenRepository, FirebaseMessaging fcm) {
        this.userDeviceTokenRepository = userDeviceTokenRepository;
        this.fcm = fcm;
    }

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
    String projectId = FirebaseApp.getInstance().getOptions().getProjectId();
    logger.info("🔥 Backend đang gửi thông báo tới dự án Firebase có ID: {}", projectId);
} catch (Exception e) {
    logger.error("Backend chưa khởi tạo Firebase App!");
}
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        try {
            logger.info("Sending batch notification to {} tokens", tokens.size());
            
            WebpushConfig webpushConfig = WebpushConfig.builder()
                    .setNotification(
                            WebpushNotification.builder()
                            .setTitle("Bkhouse thông báo")
                                    .setBody(message).build())
                    .setFcmOptions(
                            WebpushFcmOptions.builder()
                                    .setLink(link).build())
                    .build();

            Notification fcmNotification = Notification.builder()
                    .setTitle("Bkhouse thông báo")
                    .setBody(message)
                    .build();

            // Firebase hỗ trợ tối đa 500 tokens/lần. Nếu list của bác to hơn 500, cần chia nhỏ list ra.
            // Ở đây giả định list < 500.
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setWebpushConfig(webpushConfig)
                    .setNotification(fcmNotification)
                    .build();
            try {
    logger.info("📩 Gửi tới các Token: {}", tokens);
    logger.info("📩 Body thông báo: {}", message);
} catch (Exception e) {}

            BatchResponse response = fcm.sendEachForMulticast(multicastMessage);
            logger.info("Successfully sent {} messages. Failed: {}", response.getSuccessCount(), response.getFailureCount());

            // 🚨 THÊM ĐOẠN NÀY ĐỂ BẮT TẬN TAY LỖI:
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        logger.error("❌ Lý do từ chối Token: {}", responses.get(i).getException().getMessage());
                    }
                }
            }

        } catch (FirebaseMessagingException e) {
            logger.error("Error sending FCM message: ", e);
        }
    }
}
