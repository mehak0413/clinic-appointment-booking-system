package com.clinic.appointmentservice.service;

import com.clinic.appointmentservice.client.PrescriptionHistoryClient;
import com.clinic.appointmentservice.dto.PrescriptionHistory;
import com.clinic.appointmentservice.dto.PrescriptionRequest;
import com.clinic.appointmentservice.dto.PrescriptionResponse;
import com.clinic.appointmentservice.entity.Appointment;
import com.clinic.appointmentservice.entity.Prescription;
import com.clinic.appointmentservice.exception.ResourceNotFoundException;
import com.clinic.appointmentservice.repository.AppointmentRepository;
import com.clinic.appointmentservice.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionHistoryClient prescriptionHistoryClient; // Feign Client

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository,
            PrescriptionHistoryClient prescriptionHistoryClient) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionHistoryClient = prescriptionHistoryClient;
    }

    public PrescriptionResponse createPrescription(PrescriptionRequest prescription2) {
        try {
            Appointment appointment = appointmentRepository.findById(prescription2.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

            // Build Prescription entity
            Prescription prescription = new Prescription();
            prescription.setAppointment(appointment);
            prescription.setMedication(prescription2.getMedication());
            prescription.setDosage(prescription2.getDosage());
            prescription.setInstructions(prescription2.getInstructions());

            // Save locally
            Prescription saved = prescriptionRepository.save(prescription);

            // Send data to microservice

            PrescriptionHistory history = new PrescriptionHistory();
            history.setAppointmentId(appointment.getId());
            history.setDoctorId(appointment.getDoctor().getId());
            history.setPatientId(appointment.getPatient().getId());
            history.setMedication(saved.getMedication());
            history.setDosage(saved.getDosage());
            history.setInstructions(saved.getInstructions());
            history.setIssuedAt(LocalDateTime.now().toString());

            prescriptionHistoryClient.createPrescriptionHistory(history);

            return mapToResponse(saved);
        } catch (Exception e) {
            System.err.println("!!! Exception in createPrescription: " + e.getMessage());
            e.printStackTrace();
            throw e; // rethrow so Spring returns 500
        }
    }

    // ---------------- READ ----------------
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Optional<PrescriptionResponse> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .map(this::mapToResponse);
    }

    public List<PrescriptionResponse> getPrescriptionsByAppointmentId(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        return prescriptionRepository.findByAppointment(appointment).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ---------------- READ by Doctor ----------------
    public List<PrescriptionResponse> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByAppointment_Doctor_Id(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ---------------- DELETE ----------------
    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }

    public PrescriptionResponse mapToResponse(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getAppointment().getId(),
                prescription.getAppointment().getDoctor().getName(),
                prescription.getAppointment().getPatient().getName(),
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getInstructions(),
                LocalDateTime.now().toString());
    }

}
