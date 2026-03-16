package com.hospital.management.dto;

import com.hospital.management.model.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {
    private Long id;
    private PatientDTO patient;
    private DoctorDTO doctor;
    private LocalDateTime slotDatetime;
    private AppointmentStatus status;
    private String notes;
}
