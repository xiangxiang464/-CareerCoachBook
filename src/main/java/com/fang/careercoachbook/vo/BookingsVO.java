package com.fang.careercoachbook.vo;

import com.fang.careercoachbook.entity.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingsVO {

    // 1. 前端点击“取消”按钮时，需要把这个ID传回来
    private String bookingUid;

    // 2. 导师名称
    private String coachName;

    // 3. 预约状态
    private BookingStatus status;

    // 4. 预约时间段
    // 加上 @JsonFormat 是为了保证返回标准的 ISO 格式，带上 Z 代表 UTC
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endTime;

    // 5. 视频会议链接
    private String meetingUrl;
}
