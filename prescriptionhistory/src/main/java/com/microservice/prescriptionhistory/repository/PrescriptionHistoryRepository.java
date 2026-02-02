package com.microservice.prescriptionhistory.repository;

import com.microservice.prescriptionhistory.entity.PrescriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionHistoryRepository extends JpaRepository<PrescriptionHistory, Long> {

    List<PrescriptionHistory> findByPatientId(Long patientId);

    List<PrescriptionHistory> findByDoctorId(Long doctorId);

    List<PrescriptionHistory> findByAppointmentId(Long appointmentId);
}
