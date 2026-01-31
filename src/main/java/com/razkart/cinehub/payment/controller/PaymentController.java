package com.razkart.cinehub.payment.controller;

import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.payment.dto.PaymentCallbackRequest;
import com.razkart.cinehub.payment.dto.PaymentRequest;
import com.razkart.cinehub.payment.dto.PaymentResponse;
import com.razkart.cinehub.payment.service.PaymentService;
import com.razkart.cinehub.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Initiate payment", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User currentUser) {

        PaymentResponse payment = paymentService.initiatePayment(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Payment initiated"));
    }

    @PostMapping("/callback")
    @Operation(summary = "Handle payment gateway callback")
    public ResponseEntity<ApiResponse<PaymentResponse>> handleCallback(
            @Valid @RequestBody PaymentCallbackRequest callback) {

        PaymentResponse payment = paymentService.handleCallback(callback);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long paymentId) {

        PaymentResponse payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payments for a booking", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByBooking(
            @PathVariable Long bookingId) {

        List<PaymentResponse> payments = paymentService.getPaymentsByBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Initiate refund", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> initiateRefund(
            @PathVariable Long paymentId,
            @RequestBody(required = false) RefundRequest request) {

        String reason = request != null ? request.reason() : "User requested refund";
        paymentService.initiateRefund(paymentId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Refund initiated"));
    }

    public record RefundRequest(String reason) {}
}
