package com.clinic.appointmentservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    private String medication;
    private String dosage;
    private String instructions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    // Constructors
    public Prescription() {
    }

    public Prescription(Appointment appointment, String medication, String dosage, String instructions) {
        this.appointment = appointment;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
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

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
    }

}
