package com.hospital.management.service;

import com.hospital.management.dto.*;
import com.hospital.management.model.*;
import com.hospital.management.repository.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final EmailService emailService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    /**
     * Step 1: Create a Razorpay order for the consultation fee.
     * Returns order ID and public key to the frontend to launch Razorpay checkout.
     */
    public PaymentOrderResponse createOrder(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        BigDecimal fee = doctor.getConsultationFee() != null
                ? doctor.getConsultationFee()
                : BigDecimal.valueOf(100);

        // Razorpay expects amount in smallest unit (paise for INR)
        int amountInPaise = fee.multiply(BigDecimal.valueOf(100)).intValue();

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderReq = new JSONObject();
            orderReq.put("amount", amountInPaise);
            orderReq.put("currency", "INR");
            orderReq.put("receipt", "appt_" + System.currentTimeMillis());
            orderReq.put("payment_capture", 1); // Auto-capture

            Order order = client.orders.create(orderReq);
            String orderId = order.get("id");

            String doctorName = doctor.getUser().getName();
            String spec = doctor.getSpecialization() != null ? doctor.getSpecialization() : "General";

            log.info("Razorpay order created: {} for doctor: {} amount: ₹{}", orderId, doctorName, fee);

            return PaymentOrderResponse.builder()
                    .razorpayOrderId(orderId)
                    .currency("INR")
                    .amount(fee)
                    .keyId(razorpayKeyId)
                    .doctorName(doctorName)
                    .specialization(spec)
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }
    }

    /**
     * Step 2: Verify the Razorpay payment signature (HMAC-SHA256).
     * If valid → book appointment → auto-generate PAID bill.
     */
    @Transactional
    public AppointmentResponseDTO verifyAndBook(Long patientUserId, PaymentVerifyRequest req) {
        // --- Signature Verification ---
        if (!verifySignature(req.getRazorpayOrderId(), req.getRazorpayPaymentId(), req.getRazorpaySignature())) {
            throw new RuntimeException("Payment verification failed: Invalid signature.");
        }

        log.info("Payment verified for Razorpay paymentId: {}", req.getRazorpayPaymentId());

        // --- Book Appointment ---
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseGet(() -> {
                    User user = userRepository.findById(patientUserId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Patient newPatient = Patient.builder().user(user).build();
                    return patientRepository.save(newPatient);
                });

        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime slotDatetime = LocalDateTime.parse(req.getSlotDatetime());

        if (appointmentRepository.existsByDoctorIdAndSlotDatetimeAndStatusIn(
                doctor.getId(), slotDatetime,
                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED))) {
            throw new RuntimeException("Slot conflict: Doctor is already booked for this time.");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .slotDatetime(slotDatetime)
                .status(AppointmentStatus.CONFIRMED)
                .notes(req.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);

        // --- Auto-generate a PAID Bill ---
        BigDecimal fee = doctor.getConsultationFee() != null
                ? doctor.getConsultationFee()
                : BigDecimal.valueOf(100);

        Bill bill = Bill.builder()
                .appointment(appointment)
                .totalAmount(fee)
                .status(BillStatus.PAID)
                .build();
        billRepository.save(bill);

        log.info("Appointment #{} booked & bill marked PAID (Razorpay: {})",
                appointment.getId(), req.getRazorpayPaymentId());

        // --- Send confirmation email ---
        try {
            emailService.sendAppointmentConfirmation(
                    patient.getUser().getEmail(),
                    patient.getUser().getName(),
                    doctor.getUser().getName(),
                    appointment.getSlotDatetime()
            );
        } catch (Exception e) {
            log.warn("Email notification failed (non-critical): {}", e.getMessage());
        }

        // Map to DTO
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setSlotDatetime(appointment.getSlotDatetime());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    // ---- HMAC-SHA256 Signature Verification ----
    private boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {
        try {
            String data = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String generatedSig = Hex.encodeHexString(hash);
            return generatedSig.equals(razorpaySignature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }
}
