package com.hospital.management.service;

import com.hospital.management.dto.*;
import com.hospital.management.model.*;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        long totalPatients = userRepository.countByRole(Role.PATIENT);
        long totalDoctors = userRepository.countByRole(Role.DOCTOR);
        long appointmentsToday = appointmentRepository.countBySlotDatetimeBetween(startOfDay, endOfDay);
        
        // As a proxy, consider 'total active working doctors' as 'availableDoctors'. 
        // Can be dynamically adjusted via specific scheduling filters later.
        long availableDoctors = totalDoctors;

        return DashboardStatsDTO.builder()
                .totalPatients(totalPatients)
                .totalDoctors(totalDoctors)
                .appointmentsToday(appointmentsToday)
                .availableDoctors(availableDoctors)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapUserToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream().map(this::mapUserToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsFiltered(LocalDate date, Long doctorId, AppointmentStatus status) {
        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (date != null) {
                predicates.add(cb.between(root.<LocalDateTime>get("slotDatetime"), date.atStartOfDay(), date.atTime(LocalTime.MAX)));
            }
            if (doctorId != null) {
                predicates.add(cb.equal(root.join("doctor").get("id"), doctorId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return appointmentRepository.findAll(spec).stream()
                .map(this::mapAppointmentToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    private UserDTO mapUserToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private AppointmentResponseDTO mapAppointmentToDTO(Appointment appt) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appt.getId());
        dto.setSlotDatetime(appt.getSlotDatetime());
        dto.setStatus(appt.getStatus());
        dto.setNotes(appt.getNotes());
        
        dto.setPatient(patientService.mapToDTO(appt.getPatient()));
        dto.setDoctor(doctorService.mapToDTO(appt.getDoctor()));
        return dto;
    }
}
