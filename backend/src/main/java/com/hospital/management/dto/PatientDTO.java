package com.hospital.management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private LocalDate dob;
    private String bloodGroup;
    private String phone;
    private String address;
}
