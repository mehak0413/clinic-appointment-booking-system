package com.microservice.prescriptionhistory.client;

import com.microservice.prescriptionhistory.dto.AppointmentDTO;
import com.microservice.prescriptionhistory.dto.DoctorDTO;
import com.microservice.prescriptionhistory.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service", url = "http://localhost:8081", configuration = FeignClientConfig.class)
public interface AppointmentServiceClient {

    @GetMapping("/api/appointments/{id}")
    AppointmentDTO getAppointmentById(@PathVariable("id") Long appointmentId);

    @GetMapping("/api/doctors/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") Long doctorId);

    @GetMapping("/api/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") Long patientId);
}
