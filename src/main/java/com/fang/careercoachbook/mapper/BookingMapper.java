package com.fang.careercoachbook.mapper;

import com.fang.careercoachbook.entity.Booking;
import com.fang.careercoachbook.entity.BookingStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingMapper {

    // 插入新预约
    @Insert("INSERT INTO bookings (user_id, booking_uid, coach_name, coach_email, user_name, user_email, meeting_url, start_time, end_time, status, create_time, update_time) " +
            "VALUES (#{userId}, #{bookingUid}, #{coachName}, #{coachEmail}, #{userName}, #{userEmail}, #{meetingUrl}, #{startTime}, #{endTime}, #{status}, #{createTime}, #{updateTime})")
    void insert(Booking booking);

    // 根据用户ID查询列表
    @Select("SELECT * FROM bookings WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Booking> getByUserId(String userId);

    // 根据 Cal.com 的 UID 更新状态（处理 Webhook 取消事件）
    @Update("UPDATE bookings SET status = #{status}, update_time = #{updateTime} WHERE booking_uid = #{bookingUid}")
    void updateStatus(String bookingUid, BookingStatus status, LocalDateTime updateTime); // 新增参数

    // 根据 UID 查询单个预约
    @Select("SELECT * FROM bookings WHERE booking_uid = #{bookingUid}")
    Booking getByUid(String bookingUid);
}