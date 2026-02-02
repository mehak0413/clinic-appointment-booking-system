package com.clinic.appointmentservice.security;

import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.service.DoctorService;
import com.clinic.appointmentservice.service.PatientService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    private final PatientService patientService;
    private final DoctorService doctorService;

    public SecurityService(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public boolean isSelf(Long patientId, Authentication auth) {
        Patient patient = patientService.findByEmail(auth.getName()).orElse(null);
        return patient != null && patient.getId().equals(patientId);
    }

    public boolean isSelfDoctor(Long doctorId, Authentication auth) {
        Doctor doctor = doctorService.findByEmail(auth.getName()).orElse(null);
        return doctor != null && doctor.getId().equals(doctorId);
    }
}
