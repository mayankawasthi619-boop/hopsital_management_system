package com.hospital.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentVerifyRequest {

    @NotBlank
    private String razorpayOrderId;

    @NotBlank
    private String razorpayPaymentId;

    @NotBlank
    private String razorpaySignature;

    // Appointment info to book after payment success
    @NotNull
    private Long doctorId;

    @NotBlank
    private String slotDatetime;

    private String notes;
}
