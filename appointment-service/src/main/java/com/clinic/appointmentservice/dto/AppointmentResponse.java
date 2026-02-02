package com.clinic.appointmentservice.dto;

import java.time.LocalDateTime;
import com.clinic.appointmentservice.entity.enums.AppointmentStatus;

public class AppointmentResponse {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private LocalDateTime deletedAt;

    public AppointmentResponse(Long id, Long doctorId, String doctorName,
            Long patientId, String patientName,
            LocalDateTime appointmentTime, AppointmentStatus status, LocalDateTime deletedAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}
