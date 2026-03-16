package com.hospital.management.service;

import com.hospital.management.dto.PatientDTO;
import com.hospital.management.dto.PatientProfileUpdateRequest;
import com.hospital.management.model.Patient;
import com.hospital.management.model.User;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PatientDTO getPatientProfile(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyPatient(userId));
        return mapToDTO(patient);
    }

    @Transactional
    public PatientDTO updatePatientProfile(Long userId, PatientProfileUpdateRequest request) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyPatient(userId));
        
        patient.setDob(request.getDob());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        
        return mapToDTO(patientRepository.save(patient));
    }

    private Patient createEmptyPatient(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Patient patient = Patient.builder().user(user).build();
        return patientRepository.save(patient);
    }

    public PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setUserId(patient.getUser().getId());
        dto.setName(patient.getUser().getName());
        dto.setEmail(patient.getUser().getEmail());
        dto.setDob(patient.getDob());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setPhone(patient.getPhone());
        dto.setAddress(patient.getAddress());
        return dto;
    }
}
