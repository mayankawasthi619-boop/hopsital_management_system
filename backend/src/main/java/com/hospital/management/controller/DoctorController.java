package com.hospital.management.controller;

import com.hospital.management.dto.DoctorDTO;
import com.hospital.management.dto.DoctorProfileUpdateRequest;
import com.hospital.management.security.UserDetailsImpl;
import com.hospital.management.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // Publicly accessible for authenticated users to search doctors
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Long departmentId) {
        return ResponseEntity.ok(doctorService.searchDoctors(specialization, departmentId));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDTO> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(doctorService.getDoctorProfile(userDetails.getId()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody DoctorProfileUpdateRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctorProfile(userDetails.getId(), request));
    }

    // Admins viewing specific doctor profile
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDTO> getDoctorProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(doctorService.getDoctorProfile(userId));
    }
}
