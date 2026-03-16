package com.hospital.management.controller;

import com.hospital.management.dto.BillDTO;
import com.hospital.management.dto.BillRequestDTO;
import com.hospital.management.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/generate/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<BillDTO> generateBill(
            @PathVariable Long appointmentId,
            @Valid @RequestBody BillRequestDTO request) {
        return ResponseEntity.ok(billingService.generateBill(appointmentId, request));
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BillDTO> getBill(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billingService.getBill(appointmentId));
    }

    @PutMapping("/{appointmentId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<BillDTO> markAsPaid(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billingService.markAsPaid(appointmentId));
    }

    @GetMapping(value = "/{appointmentId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) BigDecimal medicineFees,
            @RequestParam(required = false) BigDecimal labFees) {
        
        byte[] pdfBytes = billingService.generatePdfBill(appointmentId, medicineFees, labFees);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "invoice_" + appointmentId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
