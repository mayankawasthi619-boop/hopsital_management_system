package com.hospital.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentOrderRequest {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Slot datetime is required")
    private String slotDatetime;

    private String notes;
}
