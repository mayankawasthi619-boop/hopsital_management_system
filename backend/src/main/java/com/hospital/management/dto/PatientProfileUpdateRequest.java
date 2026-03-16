package com.hospital.management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientProfileUpdateRequest {
    private LocalDate dob;
    private String bloodGroup;
    private String phone;
    private String address;
}
