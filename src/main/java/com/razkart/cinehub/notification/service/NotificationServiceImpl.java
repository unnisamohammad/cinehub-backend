package com.razkart.cinehub.notification.service;

import com.razkart.cinehub.notification.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;

    private static final String NOTIFICATION_EXCHANGE = "cinehub.notifications";
    private static final String NOTIFICATION_ROUTING_KEY = "notification.send";

    @Override
    public void sendNotification(NotificationPayload payload) {
        try {
            rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_ROUTING_KEY, payload);
            log.info("Notification queued: {} to {}", payload.type(), payload.recipient());
        } catch (Exception e) {
            log.error("Failed to queue notification: {}", e.getMessage());
        }
    }

    @Override
    public void sendBookingConfirmation(String email, String bookingNumber, String eventTitle,
                                         String showDate, String showTime, String venueName, String seats) {
        NotificationPayload payload = NotificationPayload.bookingConfirmation(
                email, bookingNumber, eventTitle, showDate, showTime, venueName, seats
        );
        sendNotification(payload);
    }

    @Override
    public void sendBookingCancellation(String email, String bookingNumber, String eventTitle) {
        NotificationPayload payload = NotificationPayload.bookingCancellation(
                email, bookingNumber, eventTitle
        );
        sendNotification(payload);
    }
}
