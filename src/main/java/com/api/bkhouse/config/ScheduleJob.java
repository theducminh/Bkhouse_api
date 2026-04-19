package com.api.bkhouse.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api.bkhouse.service.NotifyService;
import com.api.bkhouse.service.RealEstatePostService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class ScheduleJob {
    private Logger logger = LoggerFactory.getLogger(ScheduleJob.class);

    @Autowired
    private RealEstatePostService realEstatePostService;

    @Autowired
    private NotifyService notifyService;

    @Scheduled(cron = "59 59 23 * * *", zone = "GMT+7:00")
    public void disablePostExpire() {
//        logger.info("crontab work");
        realEstatePostService.disablePostExpire();
    }

    @Scheduled(cron = "59 59 23 * * *", zone = "GMT+7:00")
    public void notifyToSpecialAccount() {
        notifyService.thongBaoChuaDongTien();
        notifyService.thongBaoDenHanDongTien();
    }

    @Scheduled(cron = "00 59 23 * * *", zone = "GMT+7:00")
    public void statisticPricePerAreaUnit() {
        Date currDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = simpleDateFormat.format(currDate);
        realEstatePostService.calculatePricePerAreaUnit(dateFormat);
    }
}
