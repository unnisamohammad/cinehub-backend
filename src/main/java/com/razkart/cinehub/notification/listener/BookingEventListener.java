package com.razkart.cinehub.notification.listener;

import com.razkart.cinehub.notification.dto.NotificationPayload;
import com.razkart.cinehub.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {

    private final EmailService emailService;

    @RabbitListener(queues = "cinehub.notifications.queue")
    public void handleNotification(NotificationPayload payload) {
        log.info("Processing notification: {} for {}", payload.type(), payload.recipient());

        try {
            String body = buildEmailBody(payload);
            emailService.sendEmail(payload.recipient(), payload.subject(), body);
        } catch (Exception e) {
            log.error("Error processing notification: {}", e.getMessage());
        }
    }

    private String buildEmailBody(NotificationPayload payload) {
        return switch (payload.type()) {
            case "BOOKING_CONFIRMATION" -> buildBookingConfirmationBody(payload);
            case "BOOKING_CANCELLATION" -> buildBookingCancellationBody(payload);
            default -> "Notification from CineHub";
        };
    }

    private String buildBookingConfirmationBody(NotificationPayload payload) {
        return String.format("""
            Dear Customer,

            Your booking has been confirmed!

            Booking Number: %s
            Event: %s
            Date: %s
            Time: %s
            Venue: %s
            Seats: %s

            Please show this email or your ticket QR code at the venue.

            Thank you for choosing CineHub!

            Best regards,
            CineHub Team
            """,
                payload.data().get("bookingNumber"),
                payload.data().get("eventTitle"),
                payload.data().get("showDate"),
                payload.data().get("showTime"),
                payload.data().get("venueName"),
                payload.data().get("seats")
        );
    }

    private String buildBookingCancellationBody(NotificationPayload payload) {
        return String.format("""
            Dear Customer,

            Your booking has been cancelled.

            Booking Number: %s
            Event: %s

            If you paid for this booking, a refund will be processed within 5-7 business days.

            Thank you for using CineHub.

            Best regards,
            CineHub Team
            """,
                payload.data().get("bookingNumber"),
                payload.data().get("eventTitle")
        );
    }
}
