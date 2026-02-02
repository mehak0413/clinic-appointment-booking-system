// AppointmentDTO.java
package com.microservice.prescriptionhistory.dto;

public class AppointmentDTO {
    private Long id;
    private Long doctorId;
    private Long patientId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
}
