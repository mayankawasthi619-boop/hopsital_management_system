package com.hospital.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {

    private String razorpayOrderId;
    private String currency;
    private BigDecimal amount;        // consultation fee
    private String keyId;             // Razorpay public key (sent to frontend)
    private String doctorName;
    private String specialization;
}
