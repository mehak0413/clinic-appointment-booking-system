package com.microservice.prescriptionhistory.controller;

import com.microservice.prescriptionhistory.entity.PrescriptionHistory;
import com.microservice.prescriptionhistory.service.PrescriptionHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class PrescriptionHistoryController {

    private final PrescriptionHistoryService service;

    public PrescriptionHistoryController(PrescriptionHistoryService service) {
        this.service = service;
    }

    // Save history (called by other services)
    @PostMapping
    public ResponseEntity<PrescriptionHistory> saveHistory(@RequestBody PrescriptionHistory history) {
        return ResponseEntity.ok(service.savePrescriptionHistory(history));
    }

    // Get all history records
    @GetMapping
    public ResponseEntity<List<PrescriptionHistory>> getAllHistory() {
        return ResponseEntity.ok(service.getAllHistory());
    }

    // Get history by patientId
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionHistory>> getHistoryByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getHistoryByPatientId(patientId));
    }

    // Get history by doctorId
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionHistory>> getHistoryByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(service.getHistoryByDoctorId(doctorId));
    }

    // Get history by appointmentId
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<PrescriptionHistory>> getHistoryByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(service.getHistoryByAppointmentId(appointmentId));
    }
}
