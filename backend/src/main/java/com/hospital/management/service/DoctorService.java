package com.hospital.management.service;

import com.hospital.management.dto.DoctorDTO;
import com.hospital.management.dto.DoctorProfileUpdateRequest;
import com.hospital.management.model.Department;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.User;
import com.hospital.management.repository.DepartmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorProfile(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyDoctor(userId));
        return mapToDTO(doctor);
    }

    @Transactional
    public DoctorDTO updateDoctorProfile(Long userId, DoctorProfileUpdateRequest request) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyDoctor(userId));
        
        doctor.setSpecialization(request.getSpecialization());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
        
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found."));
            doctor.setDepartment(dept);
        } else {
            // Nullifying department
            doctor.setDepartment(null);
        }
        
        return mapToDTO(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> searchDoctors(String specialization, Long departmentId) {
        if (specialization != null && !specialization.isBlank()) {
            return doctorRepository.findBySpecializationContainingIgnoreCase(specialization)
                    .stream().map(this::mapToDTO).collect(Collectors.toList());
        } else if (departmentId != null) {
            return doctorRepository.findByDepartmentId(departmentId)
                    .stream().map(this::mapToDTO).collect(Collectors.toList());
        }
        return getAllDoctors();
    }

    private Doctor createEmptyDoctor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Doctor doctor = Doctor.builder().user(user).build();
        return doctorRepository.save(doctor);
    }

    public DoctorDTO mapToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUserId(doctor.getUser().getId());
        dto.setName(doctor.getUser().getName());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setConsultationFee(doctor.getConsultationFee());
        
        if (doctor.getDepartment() != null) {
            dto.setDepartment(departmentService.mapToDTO(doctor.getDepartment()));
        }
        
        return dto;
    }
}
