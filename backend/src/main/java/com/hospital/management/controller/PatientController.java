package com.hospital.management.controller;

import com.hospital.management.dto.PatientDTO;
import com.hospital.management.dto.PatientProfileUpdateRequest;
import com.hospital.management.security.UserDetailsImpl;
import com.hospital.management.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientDTO> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(patientService.getPatientProfile(userDetails.getId()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PatientProfileUpdateRequest request) {
        return ResponseEntity.ok(patientService.updatePatientProfile(userDetails.getId(), request));
    }
    
    // Admins and Doctors viewing specific patient profile
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<PatientDTO> getPatientProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(patientService.getPatientProfile(userId));
    }
}
