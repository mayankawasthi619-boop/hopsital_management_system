package com.hospital.management.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequestDTO {
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Date and Time are required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime slotDatetime;

    private String notes;
}
