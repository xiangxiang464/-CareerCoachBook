package com.fang.careercoachbook.service;

import com.fang.careercoachbook.entity.Booking;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public interface BookingService {
    String generateBookingUrl(String userId);
    void processWebhook(JsonNode payload);
}