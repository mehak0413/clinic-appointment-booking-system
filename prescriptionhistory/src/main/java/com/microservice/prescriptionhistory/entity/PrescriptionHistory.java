package com.microservice.prescriptionhistory.entity;

import jakarta.persistence.*;

//import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class PrescriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private String medication;
    private String dosage;
    private String instructions;
    private String issuedAt;

    // Default constructor
    public PrescriptionHistory() {
    }

    // Parameterized constructor
    public PrescriptionHistory(Long appointmentId, Long doctorId, Long patientId,
            String medication, String dosage, String instructions,String issuedAt) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
        this.issuedAt = issuedAt;
    }

    // Getters and Setters

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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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
