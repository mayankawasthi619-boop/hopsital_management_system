package com.hospital.management.dto;

import lombok.Data;

@Data
public class PrescriptionDTO {
    private Long id;
    private Long appointmentId;
    private String diagnosis;
    private String medicines;
    private String instructions;
}
