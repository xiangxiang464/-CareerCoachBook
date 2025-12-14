package com.fang.careercoachbook.service;

import com.fang.careercoachbook.entity.Booking;
import com.fang.careercoachbook.vo.BookingsVO;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public interface BookingService {
    String generateBookingUrl(String userId);
    List<BookingsVO> getMyBookings(String userId);
    String getCancelUrl(String bookingUid);
    void processWebhook(JsonNode payload);
}