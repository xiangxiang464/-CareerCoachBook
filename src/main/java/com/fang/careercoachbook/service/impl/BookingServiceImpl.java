package com.fang.careercoachbook.service.impl;
import com.fang.careercoachbook.common.constant.CalWebhooks;
import com.fang.careercoachbook.common.exception.BusinessException;
import com.fang.careercoachbook.entity.BookingStatus;
import com.fang.careercoachbook.mapper.BookingMapper;
import com.fang.careercoachbook.service.BookingService;
import com.fang.careercoachbook.entity.Booking;
import com.fang.careercoachbook.vo.BookingsVO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    //  Cal.com 事件链接
    private static final String CAL_BASE_URL = "https://cal.com/fang-jing-mwvuxc/30min?overlayCalendar=true";
    private static final String CANCEL_URL = "https://app.cal.com/booking/";
    @Override
    public String generateBookingUrl(String userId) {
        // 把 userId 藏在 metadata 里传给 Cal.com，这样webhook能获取到userId
        return CAL_BASE_URL + "&metadata[userId]=" + userId;
    }

    @Override
    public List<BookingsVO> getMyBookings(String userId) {
        List<Booking> bookingList = bookingMapper.getByUserId(userId);
        // 2. 创建一个空的 VO 列表，用来装转换后的数据
        List<BookingsVO> resultList = new ArrayList<>();

        // 3. 使用传统的 for 循环遍历
        for (Booking entity : bookingList) {
            BookingsVO vo = new BookingsVO();
            BeanUtils.copyProperties(entity, vo);
            resultList.add(vo);
        }
        return resultList;
    }

    @Override
    public String getCancelUrl(String bookingUid) {
        Booking booking = bookingMapper.getByUid(bookingUid);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        // 2. 校验状态：只有 "BOOKING_CREATED" 的才能取消
        // 如果已经是 PENDING, CANCELLED, ENDED，都不允许再次获取取消链接
        if (booking.getStatus() != BookingStatus.BOOKING_CREATED) {
            throw new BusinessException("当前状态不可取消");
        }
        // 3. 校验时间：如果课程已经开始（或结束），则不能取消
        // LocalDateTime.now() 获取服务器当前时间
        if (booking.getStartTime().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new BusinessException("课程已开始，无法取消");
        }
        return CANCEL_URL+bookingUid;
    }

    @Override
    @Transactional
    public void processWebhook(JsonNode jsonNode) {
        String triggerEvent = jsonNode.path("triggerEvent").asText();
        JsonNode payload = jsonNode.path("payload");

        // 用常量判断事件字符串
        if (CalWebhooks.EVENT_BOOKING_CREATED.equals(triggerEvent)) {

            // 提取基础信息
            String userId = payload.path("metadata").path("userId").asText();
            if (userId == null || userId.isEmpty()) {
                userId = "unknown_user";
            }

            String bookingUid = payload.path("uid").asText();
            String startTimeStr = payload.path("startTime").asText();
            String endTimeStr = payload.path("endTime").asText();

            //提取导师信息
            String coachEmail = payload.path("organizer").path("email").asText();
            String coachName = payload.path("organizer").path("name").asText();

            // 提取用户快照
            // 需求是1对1，所以数组中应该只有一个用户
            String userName = "";
            String userEmail = "";
            JsonNode attendees = payload.path("attendees");
            if (attendees.isArray() && !attendees.isEmpty()) {
                JsonNode firstAttendee = attendees.get(0);
                userName = firstAttendee.path("name").asText();
                userEmail = firstAttendee.path("email").asText();
            }

            // 提取视频会议链接
            // Cal.com 把 Zoom/Meet 链接放在 videoCallData.url 中
            String meetingUrl = payload.path("videoCallData").path("url").asText();
            if (meetingUrl == null || meetingUrl.isEmpty()) {
                // 也可能在 location 字段
                meetingUrl = payload.path("location").asText();
            }

            // 构建对象
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setBookingUid(bookingUid);

            // 填充快照
            booking.setCoachName(coachName);
            booking.setCoachEmail(coachEmail);
            booking.setUserName(userName);
            booking.setUserEmail(userEmail);

            // 时间转换
            booking.setStartTime(parseToUtcLocalDateTime(startTimeStr));
            booking.setEndTime(parseToUtcLocalDateTime(endTimeStr));

            // 设置枚举状态
            booking.setStatus(BookingStatus.BOOKING_CREATED);

            // 设置真实的视频链接
            booking.setMeetingUrl(meetingUrl);

            booking.setCreateTime(LocalDateTime.now(ZoneOffset.UTC));
            booking.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));

            // 6. 插入数据库
            bookingMapper.insert(booking);

        } else if (CalWebhooks.EVENT_BOOKING_CANCELLED.equals(triggerEvent)) {
            String bookingUid = payload.path("uid").asText();
            // 更新时也要用枚举
            bookingMapper.updateStatus(bookingUid, BookingStatus.BOOKING_CANCELLED);
        }
    }

    private LocalDateTime parseToUtcLocalDateTime(String isoTimeStr) {
        // 1. 解析带时区的字符串 (例如: 2023-10-10T10:00:00+09:00)
        ZonedDateTime zdt = ZonedDateTime.parse(isoTimeStr, DateTimeFormatter.ISO_DATE_TIME);

        // 2. 强转为 UTC 时间 (变成了: 2023-10-10T01:00:00Z)
        ZonedDateTime utcZdt = zdt.withZoneSameInstant(ZoneOffset.UTC);

        // 3. 转为 LocalDateTime 存入数据库
        // 此时数据库里的 01:00:00 就代表了绝对正确的 UTC 时间
        return utcZdt.toLocalDateTime();
    }
}