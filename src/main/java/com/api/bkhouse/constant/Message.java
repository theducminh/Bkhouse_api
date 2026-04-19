package com.api.bkhouse.constant;

public class Message {
    public static final String TAO_BAI_DANG = "Có bài viết mới về sự biến động giá nằm trong khu vực bạn quan tâm.";

    public static final String CAP_NHAT_BAI_DANG = "Quản trị viên đã cập nhật bài viết về sự biến động giá nằm trong khu vực bạn quan tâm.";

    public static final String TAO_REP = "Đã có bài đăng mới được đăng lên trong khu vực bạn đăng ký môi giới.";

    public static final String CAP_NHAT_REP = "Bài đăng nằm trong khu vực bạn đăng ký môi giới được cập nhật.";

    public static final String NEW_REP_ADMIN = "Có bài đăng bán/cho thuê mới chờ kiểm duyệt.";
    public static final String THONG_BAO_CHUA_DONG_TIEN = "Tài khoản doanh nghiệp của bạn chưa thực hiện đóng phí hàng tháng sau khi đăng ký. Quá hạn quy định tài khoản sẽ bị khóa";
    public static final String THONG_BAO_DEN_HAN_DONG_TIEN = "Tài khoản của bạn sắp đến hạn đóng phí hàng tháng. Hãy đảm bảo tài khoản còn đủ tiền để thanh toán cho hệ thống";
    public static final String CO_BAI_DANG_MOI_NHO_GIUP = "Có bài viết bán / cho thuê gửi yêu cầu nhờ giúp đỡ.";
    public static String getCAP_NHAT_REP_INTERESTED(String title) {
        return "Bài đăng " + title + " đã được cập nhật";
    }
}
