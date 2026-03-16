package com.hospital.management.service;

import com.hospital.management.dto.BillDTO;
import com.hospital.management.dto.BillRequestDTO;
import com.hospital.management.model.Appointment;
import com.hospital.management.model.AppointmentStatus;
import com.hospital.management.model.Bill;
import com.hospital.management.model.BillStatus;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.BillRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public BillDTO generateBill(Long appointmentId, BillRequestDTO request) {
        if (billRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new RuntimeException("Bill is already generated for this appointment.");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot generate bill. Appointment is not completed.");
        }

        BigDecimal consultationFee = appointment.getDoctor().getConsultationFee() != null 
                ? appointment.getDoctor().getConsultationFee() : BigDecimal.ZERO;
        
        BigDecimal medicineFees = request.getMedicineFees() != null ? request.getMedicineFees() : BigDecimal.ZERO;
        BigDecimal labFees = request.getLabFees() != null ? request.getLabFees() : BigDecimal.ZERO;

        BigDecimal totalAmount = consultationFee.add(medicineFees).add(labFees);

        Bill bill = Bill.builder()
                .appointment(appointment)
                .totalAmount(totalAmount)
                .status(BillStatus.PENDING)
                .build();

        return mapToDTO(billRepository.save(bill));
    }

    @Transactional(readOnly = true)
    public BillDTO getBill(Long appointmentId) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Bill not found for this appointment."));
        return mapToDTO(bill);
    }

    @Transactional
    public BillDTO markAsPaid(Long appointmentId) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Bill not found."));
        bill.setStatus(BillStatus.PAID);
        return mapToDTO(billRepository.save(bill));
    }

    @Transactional(readOnly = true)
    public byte[] generatePdfBill(Long appointmentId, BigDecimal medicineFees, BigDecimal labFees) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Bill not found."));
        
        // Default to ZERO if dynamic inputs at print aren't strictly provided 
        // Note: Realistically, line items should ideally be saved in DB if complex, 
        // but for this monolithic scope, mapping via the request is acceptable.
        BigDecimal mFees = medicineFees != null ? medicineFees : BigDecimal.ZERO;
        BigDecimal lFees = labFees != null ? labFees : BigDecimal.ZERO;
        BigDecimal cFees = bill.getAppointment().getDoctor().getConsultationFee() != null 
                ? bill.getAppointment().getDoctor().getConsultationFee() : BigDecimal.ZERO;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Hospital Management System", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            Paragraph subTitle = new Paragraph("Official Invoice");
            subTitle.setAlignment(Element.ALIGN_CENTER);
            subTitle.setSpacingAfter(20);
            document.add(subTitle);

            // Patient & Doc Details
            Font normBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("Patient Name: " + bill.getAppointment().getPatient().getUser().getName(), normBold));
            document.add(new Paragraph("Doctor: Dr. " + bill.getAppointment().getDoctor().getUser().getName()));
            document.add(new Paragraph("Date: " + bill.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"))));
            document.add(new Paragraph("Status: " + bill.getStatus().name(), normBold));
            document.add(new Paragraph(" "));

            // Line Items Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            
            // Table Header
            PdfPCell c1 = new PdfPCell(new Phrase("Description", normBold));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setPadding(8);
            table.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase("Amount ($)", normBold));
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            c2.setPadding(8);
            table.addCell(c2);

            // Cost components
            table.addCell(getCell("Consultation Fee"));
            table.addCell(getCell(cFees.toString()));

            table.addCell(getCell("Medicines"));
            table.addCell(getCell(mFees.toString()));

            table.addCell(getCell("Lab Tests"));
            table.addCell(getCell(lFees.toString()));

            // Total
            PdfPCell t1 = new PdfPCell(new Phrase("TOTAL AMOUNT", normBold));
            t1.setPadding(8);
            table.addCell(t1);

            PdfPCell t2 = new PdfPCell(new Phrase(bill.getTotalAmount().toString(), normBold));
            t2.setPadding(8);
            table.addCell(t2);

            document.add(table);
            
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private PdfPCell getCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8);
        return cell;
    }

    private BillDTO mapToDTO(Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setAppointmentId(bill.getAppointment().getId());
        dto.setPatientName(bill.getAppointment().getPatient().getUser().getName());
        dto.setDoctorName(bill.getAppointment().getDoctor().getUser().getName());
        dto.setConsultationFee(bill.getAppointment().getDoctor().getConsultationFee());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setStatus(bill.getStatus());
        dto.setGeneratedAt(bill.getGeneratedAt());
        return dto;
    }
}
