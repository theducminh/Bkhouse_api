package com.api.bkhouse.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Service
public class BenchmarkSchedulerService {

    private final JdbcTemplate jdbcTemplate;

    public BenchmarkSchedulerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Chạy vào lúc 2h00 sáng mỗi ngày
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyPriceBenchmark() {
        log.info("🚀 BẮT ĐẦU: Chạy Job tổng hợp Giá Thị Trường (Area Price Benchmark)...");
        try {
            // Gọi Function tính toán dưới PostgreSQL
            jdbcTemplate.execute("SELECT public.calculate_area_price_benchmark()");
            log.info("✅ HOÀN TẤT: Job tổng hợp Giá đã chạy thành công!");
        } catch (Exception e) {
            log.error("❌ LỖI: Quá trình tổng hợp Giá thất bại. Nguyên nhân: {}", e.getMessage());
        }
    }
}