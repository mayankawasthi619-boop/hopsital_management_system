package com.hospital.management.service;

import com.hospital.management.dto.*;
import com.hospital.management.model.*;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final EmailService emailService;

    @Transactional
    public AppointmentResponseDTO bookAppointment(Long patientUserId, AppointmentRequestDTO request) {
        // Find existing patient profile natively created by PatientService if invoked setup already. Otherwise enforce existance.
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new RuntimeException("Patient profile not initialized. Update profile first."));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (appointmentRepository.existsByDoctorIdAndSlotDatetimeAndStatusIn(
                doctor.getId(), request.getSlotDatetime(), List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED))) {
            throw new RuntimeException("Conflict: Doctor is already booked for this slot.");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .slotDatetime(request.getSlotDatetime())
                .status(AppointmentStatus.CONFIRMED) // Defaults to CONFIRMED on valid slots. Or PENDING if approval needed
                .notes(request.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);

        // Notify over Mail
        emailService.sendAppointmentConfirmation(
                patient.getUser().getEmail(),
                patient.getUser().getName(),
                doctor.getUser().getName(),
                appointment.getSlotDatetime()
        );

        return mapToDTO(appointment);
    }

    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long appointmentId, Long requestingUserId, boolean isAdminOrDoctor) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!isAdminOrDoctor && !appointment.getPatient().getUser().getId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only cancel your own appointments.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);

        emailService.sendAppointmentCancellation(
                appointment.getPatient().getUser().getEmail(),
                appointment.getPatient().getUser().getName(),
                appointment.getDoctor().getUser().getName(),
                appointment.getSlotDatetime()
        );

        return mapToDTO(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getPatientAppointments(Long patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return appointmentRepository.findByPatientId(patient.getId()).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getDoctorAppointments(Long doctorUserId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return appointmentRepository.findByDoctorId(doctor.getId()).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponseDTO markCompleted(Long appointmentId, Long doctorUserId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!appointment.getDoctor().getUser().getId().equals(doctorUserId)) {
            throw new RuntimeException("Unauthorized: You can only update your own assigned appointments.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public PrescriptionDTO addPrescription(Long appointmentId, Long doctorUserId, PrescriptionRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctor().getUser().getId().equals(doctorUserId)) {
            throw new RuntimeException("Unauthorized: Only the assigned doctor can add a prescription.");
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Appointment must be COMPLETED to add prescription.");
        }

        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElse(new Prescription());

        prescription.setAppointment(appointment);
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setMedicines(request.getMedicines());
        prescription.setInstructions(request.getInstructions());

        return mapPrescriptionToDTO(prescriptionRepository.save(prescription));
    }

    @Transactional(readOnly = true)
    public PrescriptionDTO getPrescription(Long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId)
                .map(this::mapPrescriptionToDTO)
                .orElseThrow(() -> new RuntimeException("Prescription not found for this appointment"));
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    private AppointmentResponseDTO mapToDTO(Appointment appt) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appt.getId());
        dto.setSlotDatetime(appt.getSlotDatetime());
        dto.setStatus(appt.getStatus());
        dto.setNotes(appt.getNotes());
        
        dto.setPatient(patientService.mapToDTO(appt.getPatient()));
        dto.setDoctor(doctorService.mapToDTO(appt.getDoctor()));
        return dto;
    }

    private PrescriptionDTO mapPrescriptionToDTO(Prescription p) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(p.getId());
        dto.setAppointmentId(p.getAppointment().getId());
        dto.setDiagnosis(p.getDiagnosis());
        dto.setMedicines(p.getMedicines());
        dto.setInstructions(p.getInstructions());
        return dto;
    }
}
