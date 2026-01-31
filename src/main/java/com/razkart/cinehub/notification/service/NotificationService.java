package com.razkart.cinehub.notification.service;

import com.razkart.cinehub.notification.dto.NotificationPayload;

public interface NotificationService {

    /**
     * Send notification asynchronously via RabbitMQ.
     */
    void sendNotification(NotificationPayload payload);

    /**
     * Send booking confirmation notification.
     */
    void sendBookingConfirmation(String email, String bookingNumber, String eventTitle,
                                  String showDate, String showTime, String venueName, String seats);

    /**
     * Send booking cancellation notification.
     */
    void sendBookingCancellation(String email, String bookingNumber, String eventTitle);
}
