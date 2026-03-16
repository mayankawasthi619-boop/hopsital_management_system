package com.hospital.management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DoctorDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String specialization;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private DepartmentDTO department;
}
