package com.hospital.management.controller;

import com.hospital.management.dto.AppointmentResponseDTO;
import com.hospital.management.dto.PaymentOrderResponse;
import com.hospital.management.dto.PaymentVerifyRequest;
import com.hospital.management.security.UserDetailsImpl;
import com.hospital.management.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Step 1 — Create a Razorpay order for the given doctor's consultation fee.
     * Returns the order ID and public key to the frontend.
     */
    @PostMapping("/create-order/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PaymentOrderResponse> createOrder(@PathVariable Long doctorId) {
        PaymentOrderResponse response = paymentService.createOrder(doctorId);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2 — Verify the Razorpay payment signature.
     * On success: books the appointment and marks the bill as PAID.
     */
    @PostMapping("/verify-and-book")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> verifyAndBook(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PaymentVerifyRequest request) {
        AppointmentResponseDTO appointment =
                paymentService.verifyAndBook(userDetails.getId(), request);
        return ResponseEntity.ok(appointment);
    }
}
