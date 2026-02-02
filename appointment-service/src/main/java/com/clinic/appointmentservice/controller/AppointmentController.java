package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.AppointmentRequest;
import com.clinic.appointmentservice.dto.AppointmentResponse;
import com.clinic.appointmentservice.entity.Appointment;
import com.clinic.appointmentservice.entity.CustomUserDetails;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.service.AppointmentService;
import com.clinic.appointmentservice.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;

    public AppointmentController(AppointmentService appointmentService, DoctorRepository doctorRepository) {
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
    }

    // ---------------- CREATE APPOINTMENT ----------------
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponse createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        Long loggedInPatientId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        request.setPatientId(loggedInPatientId); // Set logged-in patient in request

        // Call service
        Appointment saved = appointmentService.createAppointment(request);
        AppointmentResponse savedResponse = appointmentService.mapToResponse(saved);
        return savedResponse;
    }

    // ---------------- UPDATE APPOINTMENT ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        Long loggedInPatientId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        request.setPatientId(loggedInPatientId); // Ensure patient ID is correct

        // Update appointment via service
        var saved = appointmentService.updateAppointment(id, request);

        // Ensure doctor-patient relationship
        Doctor doctor = saved.getDoctor();
        Patient patient = saved.getPatient();
        if (!doctor.getPatients().contains(patient)) {
            doctor.addPatient(patient);
            doctorRepository.save(doctor);
        }

        AppointmentResponse response = appointmentService.mapToResponse(saved);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> updateAppointmentPartial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {

        Long loggedInPatientId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        Appointment updated = appointmentService.updateAppointmentPartial(id, updates, loggedInPatientId);
        return ResponseEntity.ok(appointmentService.mapToResponse(updated));
    }

    // ---------------- DELETE APPOINTMENT ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long id,
            Authentication authentication) {

        // Service handles checking patient internally
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- GET APPOINTMENT BY ID ----------------
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAllAppointmentsEntity()
                .stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        AppointmentResponse response = appointmentService.mapToResponse(appointment);
        return ResponseEntity.ok(response);
    }

    // ---------------- GET ALL APPOINTMENTS ----------------
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointmentsEntity().stream()
                .filter(a -> a.getDeletedAt() == null)
                .map(appointmentService::mapToResponse)
                .toList();
    }
}
