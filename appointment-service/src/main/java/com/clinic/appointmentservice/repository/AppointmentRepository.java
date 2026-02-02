package com.clinic.appointmentservice.repository;

import com.clinic.appointmentservice.entity.Appointment;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.entity.enums.AppointmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctorAndDeletedAtIsNull(Doctor doctor);

    List<Appointment> findByPatientAndDeletedAtIsNull(Patient patient);

    List<Appointment> findByDeletedAtIsNull();

    List<Appointment> findByDoctorAndStatusAndDeletedAtIsNull(Doctor doctor, AppointmentStatus status);
}
