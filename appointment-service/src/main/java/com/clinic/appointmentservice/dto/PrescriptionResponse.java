package com.clinic.appointmentservice.dto;

public class PrescriptionResponse {
    private Long id;
    private Long appointmentId;
    private String doctorName;
    private String patientName;
    private String medication;
    private String dosage;
    private String instructions;
    private String issuedAt;

    public PrescriptionResponse() {
    }

    public PrescriptionResponse(Long id, Long appointment, String doctorName, String patientName, String medication, String dosage, String instructions,
            String issuedAt) {
        this.id = id;
        this.appointmentId = appointment;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;

    }
}