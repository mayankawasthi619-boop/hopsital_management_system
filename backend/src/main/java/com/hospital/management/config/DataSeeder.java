package com.hospital.management.config;

import com.hospital.management.model.*;
import com.hospital.management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Only seed if no doctors exist at all
        if (doctorRepository.count() > 0) {
            log.info("[DataSeeder] Data already seeded. Skipping.");
            return;
        }

        log.info("[DataSeeder] Seeding demo data...");

        // --- Departments ---
        Department cardiology = getOrCreateDepartment("Cardiology");
        Department neurology  = getOrCreateDepartment("Neurology");
        Department pediatrics = getOrCreateDepartment("Pediatrics");
        Department orthopedics = getOrCreateDepartment("Orthopedics");
        Department dermatology = getOrCreateDepartment("Dermatology");

        // --- Admin user ---
        if (!userRepository.existsByEmail("admin@hospital.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@hospital.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("[DataSeeder] Admin created: admin@hospital.com / admin123");
        }

        // --- Demo Doctors ---
        createDoctor("Dr. Arjun Mehta",     "arjun.mehta@hospital.com",   "Cardiologist",       12, new BigDecimal("800"),  cardiology);
        createDoctor("Dr. Priya Sharma",    "priya.sharma@hospital.com",  "Neurologist",         9, new BigDecimal("900"),  neurology);
        createDoctor("Dr. Rohan Kapoor",    "rohan.kapoor@hospital.com",  "Pediatrician",        6, new BigDecimal("600"),  pediatrics);
        createDoctor("Dr. Sneha Patel",     "sneha.patel@hospital.com",   "Orthopedic Surgeon", 15, new BigDecimal("1000"), orthopedics);
        createDoctor("Dr. Vikram Nair",     "vikram.nair@hospital.com",   "Dermatologist",       7, new BigDecimal("700"),  dermatology);
        createDoctor("Dr. Ananya Reddy",    "ananya.reddy@hospital.com",  "General Physician",   5, new BigDecimal("500"),  null);

        // --- Demo Patient ---
        if (!userRepository.existsByEmail("patient@hospital.com")) {
            User patientUser = User.builder()
                    .name("Demo Patient")
                    .email("patient@hospital.com")
                    .password(passwordEncoder.encode("patient123"))
                    .role(Role.PATIENT)
                    .build();
            patientUser = userRepository.save(patientUser);

            Patient patient = Patient.builder().user(patientUser).build();
            patientRepository.save(patient);
            log.info("[DataSeeder] Patient created: patient@hospital.com / patient123");
        }

        log.info("[DataSeeder] ✅ Demo data seeded successfully!");
        log.info("[DataSeeder] ─────────────────────────────────────────────");
        log.info("[DataSeeder]  Login Credentials:");
        log.info("[DataSeeder]  Admin   → admin@hospital.com    / admin123");
        log.info("[DataSeeder]  Patient → patient@hospital.com  / patient123");
        log.info("[DataSeeder]  Doctor  → arjun.mehta@hospital.com / doctor123");
        log.info("[DataSeeder] ─────────────────────────────────────────────");
    }

    private Department getOrCreateDepartment(String name) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> departmentRepository.save(
                        Department.builder().name(name).build()));
    }

    private void createDoctor(String name, String email, String specialization,
                               int experience, BigDecimal fee, Department department) {
        if (userRepository.existsByEmail(email)) return;

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode("doctor123"))
                .role(Role.DOCTOR)
                .build();
        user = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(specialization)
                .experienceYears(experience)
                .consultationFee(fee)
                .department(department)
                .build();
        doctorRepository.save(doctor);
        log.info("[DataSeeder] Doctor created: {} ({})", name, email);
    }
}
