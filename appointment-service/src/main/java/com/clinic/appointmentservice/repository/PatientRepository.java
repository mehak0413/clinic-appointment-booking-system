package com.clinic.appointmentservice.repository;

import com.clinic.appointmentservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by email (for login)
    Optional<Patient> findByEmail(String email);

    // Optional: find by name
    Optional<Patient> findByName(String name);

    List<Patient> findAllByDeletedAtIsNull();

    Optional<Patient> findByIdAndDeletedAtIsNull(Long id);

    Optional<Patient> findByEmailAndDeletedAtIsNull(String email);

    List<Patient> findByNameAndDeletedAtIsNull(String name);

}
