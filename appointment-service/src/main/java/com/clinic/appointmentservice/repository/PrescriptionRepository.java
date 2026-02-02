package com.clinic.appointmentservice.repository;

import com.clinic.appointmentservice.entity.Prescription;
import com.clinic.appointmentservice.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByAppointment(Appointment appointment);
    List<Prescription> findByAppointment_Doctor_Id(Long doctorId);
    List<Prescription> findByAppointment_Patient_Id(Long patientId);
}
