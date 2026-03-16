package com.hospital.management.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillRequestDTO {
    @DecimalMin(value = "0.0", message = "Medicine fees cannot be negative")
    private BigDecimal medicineFees;

    @DecimalMin(value = "0.0", message = "Lab test fees cannot be negative")
    private BigDecimal labFees;
}
