package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.entity.enums.AppointmentStatus;
//import com.clinic.appointmentservice.dto.AppointmentRequest;
import com.clinic.appointmentservice.dto.DoctorRequest;
import com.clinic.appointmentservice.dto.DoctorResponse;
import com.clinic.appointmentservice.dto.PatientRequest;
import com.clinic.appointmentservice.entity.Appointment;
import com.clinic.appointmentservice.service.DoctorService;
import com.clinic.appointmentservice.service.PatientService;
import com.clinic.appointmentservice.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public AdminController(DoctorService doctorService, PatientService patientService,
            AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    // ------------------- DOCTOR -------------------

    @GetMapping("/doctors")
    public List<Doctor> getDoctorsOnly() {
        return doctorService.getAllDoctors();
    }

    @PostMapping("/doctors")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor created = doctorService.addDoctor(doctor);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody DoctorRequest doctor) {
        Doctor updated = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(doc -> new DoctorResponse(
                        doc.getId(),
                        doc.getName(),
                        doc.getEmail(),
                        doc.getSpecialization()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- PATIENT -------------------

    @GetMapping("/patients")
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.of(patientService.getPatientById(id));
    }

    @PostMapping("/patients")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient created = patientService.addPatient(patient);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/patients/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody PatientRequest patient) {
        Patient updated = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------- APPOINTMENT -------------------

    @GetMapping("/appointments")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointmentsEntity();
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.of(appointmentService.getAppointmentById(id));
    }

    @PatchMapping("/appointments/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        Appointment updated = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointmentByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
