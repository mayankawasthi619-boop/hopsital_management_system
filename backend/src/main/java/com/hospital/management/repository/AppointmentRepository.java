package com.hospital.management.repository;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    
    // For conflict detection
    boolean existsByDoctorIdAndSlotDatetimeAndStatusIn(Long doctorId, LocalDateTime slotDatetime, List<AppointmentStatus> statuses);

    // Dashboard Statistics
    long countBySlotDatetimeBetween(LocalDateTime start, LocalDateTime end);
}
