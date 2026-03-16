package com.hospital.management.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        log.info("[EMAIL] Password Reset Email to: {} | token: {}", to, token);
        log.info("[EMAIL] Reset link: http://localhost:3000/reset-password?token={}", token);
    }

    @Async
    public void sendAppointmentConfirmation(String to, String patientName, String doctorName, LocalDateTime slot) {
        String formattedDate = slot.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        log.info("[EMAIL] Appointment Confirmation to: {} | Patient: {} | Doctor: {} | Slot: {}", to, patientName, doctorName, formattedDate);
    }

    @Async
    public void sendAppointmentCancellation(String to, String patientName, String doctorName, LocalDateTime slot) {
        String formattedDate = slot.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        log.info("[EMAIL] Appointment Cancellation to: {} | Patient: {} | Doctor: {} | Slot: {}", to, patientName, doctorName, formattedDate);
    }
}
