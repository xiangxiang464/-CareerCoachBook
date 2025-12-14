package com.fang.careercoachbook.mapper;

import com.fang.careercoachbook.entity.Booking;
import com.fang.careercoachbook.entity.BookingStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookingMapper {

    // 插入新预约
    @Insert("INSERT INTO bookings (user_id, booking_uid, coach_name, coach_email, user_name, user_email, meeting_url, start_time, end_time, status, create_time, update_time) " +
            "VALUES (#{userId}, #{bookingUid}, #{coachName}, #{coachEmail}, #{userName}, #{userEmail}, #{meetingUrl}, #{startTime}, #{endTime}, #{status}, #{createTime}, #{updateTime})")
    void insert(Booking booking);


    // 根据 Cal.com 的 UID 更新状态（处理 Webhook 取消事件）
    @Update("UPDATE bookings SET status = #{status} WHERE booking_uid = #{bookingUid}")
    void updateStatus(String bookingUid, BookingStatus status);


}