package com.clinic.appointmentservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.clinic.appointmentservice.dto.PrescriptionHistory;

@FeignClient(name = "prescription-history-service", url = "http://localhost:8080")
public interface PrescriptionHistoryClient {

    @PostMapping("/history")
    PrescriptionHistory createPrescriptionHistory(@RequestBody PrescriptionHistory prescriptionHistory);

    @GetMapping("/history/patient/{patientId}")
    List<PrescriptionHistory> getHistoryByPatient(@PathVariable("patientId") Long patientId);

    @GetMapping("/history/doctor/{doctorId}")
    List<PrescriptionHistory> getHistoryByDoctor(@PathVariable("doctorId") Long doctorId);
}