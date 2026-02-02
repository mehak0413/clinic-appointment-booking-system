package com.microservice.prescriptionhistory.client;

import com.microservice.prescriptionhistory.dto.AppointmentDTO;
import com.microservice.prescriptionhistory.dto.DoctorDTO;
import com.microservice.prescriptionhistory.dto.PatientDTO;
import org.springframework.stereotype.Component;

@Component
public class FeignClientFallback implements AppointmentServiceClient {

    @Override
    public AppointmentDTO getAppointmentById(Long appointmentId) {
        return null; // Fallback: return null if service unavailable
    }

    @Override
    public DoctorDTO getDoctorById(Long doctorId) {
        return null; // Fallback
    }

    @Override
    public PatientDTO getPatientById(Long patientId) {
        return null; // Fallback
    }
}
