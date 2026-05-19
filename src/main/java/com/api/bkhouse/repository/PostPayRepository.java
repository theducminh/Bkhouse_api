package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.api.bkhouse.entity.PostPay;
import com.api.bkhouse.entity.response.IPaymentStatistic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostPayRepository extends JpaRepository<PostPay, Long> {
    List<PostPay> findByUserIdOrderByCreateAtDesc(UUID userId);

    //Thống kê thanh toán theo Tháng & Năm
   @Query(value = "SELECT tmp1.postPrice, tmp2.specialAccountPay FROM " +
            "(SELECT SUM(price) AS postPrice FROM post_pay WHERE EXTRACT(MONTH FROM create_at) = :month AND EXTRACT(YEAR FROM create_at) = :year) AS tmp1, " +
            "(SELECT SUM(amount) AS specialAccountPay FROM special_account_pay WHERE EXTRACT(MONTH FROM create_at) = :month AND EXTRACT(YEAR FROM create_at) = :year) AS tmp2", 
            nativeQuery = true)
    Optional<IPaymentStatistic> getPaymentStatistic(@Param("month") Integer month, @Param("year") Integer year);

    //Thống kê thanh toán theo Ngày
    @Query(value = "SELECT tmp1.postPrice, tmp2.specialAccountPay FROM " +
            "(SELECT SUM(price) AS postPrice FROM post_pay WHERE DATE(create_at) = CAST(:date AS DATE)) AS tmp1, " +
            "(SELECT SUM(amount) AS specialAccountPay FROM special_account_pay WHERE DATE(create_at) = CAST(:date AS DATE)) AS tmp2", 
            nativeQuery = true)
    Optional<IPaymentStatistic> getPaymentStatisticMonth(@Param("date") String date);
    
    //Thống kê tổng tiền đã thu được trong năm và trong tháng
    @Query(value = "SELECT SUM(so_tien) FROM charge WHERE status = 'DA_XAC_NHAN' AND EXTRACT(MONTH FROM create_at) = :month AND EXTRACT(YEAR FROM create_at) = :year", 
            nativeQuery = true)
    Long getChargeInYear(@Param("month") Integer month, @Param("year") Integer year);
    
    //Thống kê tổng tiền đã thu được trong ngày
    @Query(value = "SELECT SUM(so_tien) FROM charge WHERE status = 'DA_XAC_NHAN' AND DATE(create_at) = CAST(:date AS DATE)", 
            nativeQuery = true)
    Long getChargeInMonth(@Param("date") String date);
}
