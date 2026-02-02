package com.clinic.appointmentservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // matches existing table

    @Column(unique = true, nullable = false)
    private String email; // matches existing table

    private String name;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String password; // new column for login

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany
    @JoinTable(name = "doctor_patient", joinColumns = @JoinColumn(name = "patient_id"), inverseJoinColumns = @JoinColumn(name = "doctor_id"))
    private Set<Doctor> doctors = new HashSet<>();

    // Constructors
    public Patient() {
    }

    public Patient(String email, String name, String password, String role) {
        this.email = email;
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(Set<Doctor> doctors) {
        this.doctors = doctors;
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
    public void addDoctor(Doctor doctor) {
        this.doctors.add(doctor);
        doctor.getPatients().add(this);
    }

    public void removeDoctor(Doctor doctor) {
        this.doctors.remove(doctor);
        doctor.getPatients().remove(this);
    }

    // Helper
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
