package com.example.facebookbackend.services;

import com.example.facebookbackend.securities.jwt.AuthTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {
    private Logger logger = LoggerFactory.getLogger(ScheduleTask.class);
    @Autowired
    ReportServices reportServices;

    @Scheduled(fixedRate = 60*1000)
    public void handlePostRequest(){
        logger.info("Thực hiện xử lý báo cáo bài viết");
        reportServices.handleReportPost();
    }
}
