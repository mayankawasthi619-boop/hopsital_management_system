package com.hospital.management.dto;

import com.hospital.management.model.BillStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillDTO {
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private BigDecimal consultationFee;
    private BigDecimal totalAmount;
    private BillStatus status;
    private LocalDateTime generatedAt;
}
