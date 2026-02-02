package com.microservice.prescriptionhistory.service;

import com.microservice.prescriptionhistory.client.AppointmentServiceClient;
import com.microservice.prescriptionhistory.entity.PrescriptionHistory;
import com.microservice.prescriptionhistory.repository.PrescriptionHistoryRepository;
import feign.FeignException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionHistoryService {

    private final PrescriptionHistoryRepository repository;
    private final AppointmentServiceClient appointmentServiceClient;

    public PrescriptionHistoryService(PrescriptionHistoryRepository repository,
                                      AppointmentServiceClient appointmentServiceClient) {
        this.repository = repository;
        this.appointmentServiceClient = appointmentServiceClient;
    }

    public PrescriptionHistory savePrescriptionHistory(PrescriptionHistory history) {

        // Fetch current logged-in user from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthorized: no user logged in");
        }

        // Assume the user is a doctor; set doctorId from JWT principal (or credentials)
        Long doctorId = extractDoctorIdFromAuth(auth);
        history.setDoctorId(doctorId);

        // Optionally, you could store doctor email in the entity if needed
        // String doctorEmail = auth.getName();

        // Validate associated entities exist
        fetchEntity(() -> appointmentServiceClient.getAppointmentById(history.getAppointmentId()),
                "Invalid appointmentId");
        fetchEntity(() -> appointmentServiceClient.getPatientById(history.getPatientId()), "Invalid patientId");

        return repository.save(history);
    }

    private Long extractDoctorIdFromAuth(Authentication auth) {
        // If your JWT sets credentials as doctorId, cast it
        if (auth.getCredentials() instanceof String) {
            try {
                return Long.parseLong((String) auth.getCredentials());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid doctorId in token");
            }
        }
        throw new RuntimeException("No doctorId found in authentication token");
    }

    private <T> T fetchEntity(SupplierWithFeign<T> supplier, String errorMessage) {
        try {
            return supplier.get();
        } catch (FeignException.NotFound e) {
            throw new RuntimeException(errorMessage);
        }
    }

    public List<PrescriptionHistory> getAllHistory() {
        return repository.findAll();
    }

    public List<PrescriptionHistory> getHistoryByPatientId(Long patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<PrescriptionHistory> getHistoryByDoctorId(Long doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    public List<PrescriptionHistory> getHistoryByAppointmentId(Long appointmentId) {
        return repository.findByAppointmentId(appointmentId);
    }

    @FunctionalInterface
    interface SupplierWithFeign<T> {
        T get() throws FeignException.NotFound;
    }
}
