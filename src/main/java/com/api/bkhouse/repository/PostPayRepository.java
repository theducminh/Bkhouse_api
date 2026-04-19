package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.PostPay;
import com.api.bkhouse.entity.response.IPaymentStatistic;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostPayRepository extends JpaRepository<PostPay, Long> {
    List<PostPay> findByUserIdOrderByCreateAtDesc(String userId);
    @Query(value = "select tmp1.postPrice, tmp2.specialAccountPay from\n" +
            "(select sum(price) as postPrice from post_pay where month(create_at) = :month and year(create_at) = :year) as tmp1,\n" +
            "(select sum(amount) as specialAccountPay from special_account_pay where month(create_at) = :month and year(create_at) = :year) as tmp2;", nativeQuery = true)
    Optional<IPaymentStatistic> getPaymentStatistic(Integer month, Integer year);

    @Query(value = "select tmp1.postPrice, tmp2.specialAccountPay from\n" +
            "(select sum(price) as postPrice from post_pay where date(create_at) = :date) as tmp1,\n" +
            "(select sum(amount) as specialAccountPay from special_account_pay where date(create_at) = :date) as tmp2;", nativeQuery = true)
    Optional<IPaymentStatistic> getPaymentStatisticMonth(String date);

    @Query(value = "select sum(so_tien) from charge where status = 'DA_XAC_NHAN' and month(create_at) = :month and year(create_at) = :year", nativeQuery = true)
    Long getChargeInYear(Integer month, Integer year);

    @Query(value = "select sum(so_tien) from charge where status = 'DA_XAC_NHAN' and date(create_at) = :date", nativeQuery = true)
    Long getChargeInMonth(String date);
}
