package com.fang.careercoachbook.controller;


import com.fang.careercoachbook.common.Result;
import com.fang.careercoachbook.entity.Booking;
import com.fang.careercoachbook.service.BookingService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 获取预约链接
     */
    @PostMapping("/booking-url")
    public Result<String> getBookingUrl(@RequestParam String userId) {
        log.info("用户请求预约: {}", userId);
        String url = bookingService.generateBookingUrl(userId);
        return Result.success(url);
    }

    /**
     * Webhook 回调
     */
    @PostMapping("/webhook/cal")
    public Result<String> handleWebhook(@RequestBody JsonNode payload) {
        log.info("收到Webhook回调");
        bookingService.processWebhook(payload);
        return Result.success("Processed");
    }
}
