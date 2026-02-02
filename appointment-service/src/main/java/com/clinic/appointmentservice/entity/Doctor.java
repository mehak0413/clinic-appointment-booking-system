package com.clinic.appointmentservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // matches existing table
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email; // for login

    @Column(nullable = false)
    private String role;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany(mappedBy = "doctors")
    private Set<Patient> patients = new HashSet<>();

    // Constructors
    public Doctor() {
    }

    public Doctor(String name, String email, String specialization, String password, String role) {
        this.name = name;
        this.email = email;
        this.specialization = specialization;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Helper methods for bidirectional mapping
    public void addPatient(Patient patient) {
        this.patients.add(patient);
        patient.getDoctors().add(this);
    }

    public void removePatient(Patient patient) {
        this.patients.remove(patient);
        patient.getDoctors().remove(this);
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
}
