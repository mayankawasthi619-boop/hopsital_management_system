package com.hospital.management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DoctorProfileUpdateRequest {
    private String specialization;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private Long departmentId;
}
