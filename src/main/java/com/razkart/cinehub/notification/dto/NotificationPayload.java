package com.razkart.cinehub.notification.dto;

import java.io.Serializable;
import java.util.Map;

public record NotificationPayload(
        String type,
        String recipient,
        String subject,
        String template,
        Map<String, Object> data
) implements Serializable {

    public static NotificationPayload bookingConfirmation(String email, String bookingNumber,
                                                          String eventTitle, String showDate,
                                                          String showTime, String venueName,
                                                          String seats) {
        return new NotificationPayload(
                "BOOKING_CONFIRMATION",
                email,
                "Booking Confirmed - " + bookingNumber,
                "booking-confirmation",
                Map.of(
                        "bookingNumber", bookingNumber,
                        "eventTitle", eventTitle,
                        "showDate", showDate,
                        "showTime", showTime,
                        "venueName", venueName,
                        "seats", seats
                )
        );
    }

    public static NotificationPayload bookingCancellation(String email, String bookingNumber,
                                                           String eventTitle) {
        return new NotificationPayload(
                "BOOKING_CANCELLATION",
                email,
                "Booking Cancelled - " + bookingNumber,
                "booking-cancellation",
                Map.of(
                        "bookingNumber", bookingNumber,
                        "eventTitle", eventTitle
                )
        );
    }
}
