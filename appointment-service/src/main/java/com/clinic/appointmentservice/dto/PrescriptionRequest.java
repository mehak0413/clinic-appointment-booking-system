package com.clinic.appointmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PrescriptionRequest {
    @NotNull(message = "ID is required")
    private Long appointmentId;
    @NotBlank(message = "Medications are required")
    private String medication;
    @NotBlank(message = "Dosage is required")
    private String dosage;
    @NotBlank(message = "Instructions are required")
    private String instructions;

    public PrescriptionRequest() {}

    public PrescriptionRequest(Long appointmentId, String medication, String dosage, String instructions) {
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
