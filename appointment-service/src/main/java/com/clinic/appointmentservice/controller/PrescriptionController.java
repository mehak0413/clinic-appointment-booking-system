package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.client.PrescriptionHistoryClient;
import com.clinic.appointmentservice.dto.PrescriptionHistory;
import com.clinic.appointmentservice.dto.PrescriptionRequest;
import com.clinic.appointmentservice.dto.PrescriptionResponse;
import com.clinic.appointmentservice.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PrescriptionHistoryClient prescriptionHistoryClient;

    public PrescriptionController(PrescriptionService prescriptionService,
            PrescriptionHistoryClient prescriptionHistoryClient) {
        this.prescriptionService = prescriptionService;
        this.prescriptionHistoryClient = prescriptionHistoryClient;
    }

    // CREATE prescription (by DOCTOR)
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public PrescriptionResponse createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        return prescriptionService.createPrescription(request);
    }

    // READ all prescriptions (by ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    // READ a specific prescription by ID (by DOCTOR or PATIENT)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable Long id) {
        return prescriptionService.getPrescriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ prescriptions by doctor ID (only DOCTOR)
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<PrescriptionResponse> getByDoctor(@PathVariable Long doctorId) {
        return prescriptionService.getPrescriptionsByDoctorId(doctorId);
    }

    // READ prescriptions by appointment ID (by DOCTOR or PATIENT)
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public List<PrescriptionResponse> getByAppointment(@PathVariable Long appointmentId) {
        return prescriptionService.getPrescriptionsByAppointmentId(appointmentId);
    }

    // DELETE prescription by ID (by DOCTOR)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test-history-feign")
    public String testFeign() {
        try {
            PrescriptionHistory history = new PrescriptionHistory();
            history.setAppointmentId(1L);
            history.setDoctorId(1L);
            history.setPatientId(1L);
            history.setMedication("Paracetamol");
            history.setDosage("500mg");
            history.setInstructions("2 times a day");
            history.setIssuedAt(LocalDateTime.now().toString());

            prescriptionHistoryClient.createPrescriptionHistory(history);

            return "Feign call success!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Feign call failed: " + e.getMessage();
        }
    }

}