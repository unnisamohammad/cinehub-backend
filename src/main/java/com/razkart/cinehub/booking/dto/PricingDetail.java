package com.razkart.cinehub.booking.dto;

import java.math.BigDecimal;

public record PricingDetail(
        BigDecimal ticketAmount,
        BigDecimal convenienceFee,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {}
