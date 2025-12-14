package com.fang.careercoachbook.entity;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private String userId;
    private String bookingUid;
    private String coachName;
    private String coachEmail;
    private String userName;   // 用户在 Cal 填写的姓名
    private String userEmail;  // 用户在 Cal 填写的邮箱
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status; // PENDING, BOOKING_CREATED,BOOKING_CANCELLED,MEETING_ENDED,NO_SHOW.
    private String meetingUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}