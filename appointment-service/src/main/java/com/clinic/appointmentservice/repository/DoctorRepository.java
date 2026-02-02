package com.clinic.appointmentservice.repository;

import com.clinic.appointmentservice.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Find doctor by name
    Optional<Doctor> findByName(String name);

    // You can add email field in Doctor if needed, then:
    Optional<Doctor> findByEmail(String email);

    // Custom method to fetch only doctors
    List<Doctor> findByRole(String role);

    List<Doctor> findAllByDeletedAtIsNull();

    Optional<Doctor> findByIdAndDeletedAtIsNull(Long id);

    Optional<Doctor> findByEmailAndDeletedAtIsNull(String email);

    List<Doctor> findByRoleAndDeletedAtIsNull(String role);

}
