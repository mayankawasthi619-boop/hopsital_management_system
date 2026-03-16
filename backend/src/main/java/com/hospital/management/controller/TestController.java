package com.hospital.management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public String patientAccess() {
        return "Patient Content.";
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public String doctorAccess() {
        return "Doctor Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
