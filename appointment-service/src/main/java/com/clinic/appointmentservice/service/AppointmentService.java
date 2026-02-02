package com.clinic.appointmentservice.service;

import com.clinic.appointmentservice.dto.AppointmentRequest;
import com.clinic.appointmentservice.dto.AppointmentResponse;
import com.clinic.appointmentservice.entity.Appointment;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.entity.enums.AppointmentStatus;
import com.clinic.appointmentservice.exception.ResourceNotFoundException;
import com.clinic.appointmentservice.exception.UnauthorizedException;
import com.clinic.appointmentservice.repository.AppointmentRepository;
import com.clinic.appointmentservice.repository.DoctorRepository;
import com.clinic.appointmentservice.repository.PatientRepository;
import com.clinic.appointmentservice.entity.CustomUserDetails;

import jakarta.transaction.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // -------------------- Authentication Helper --------------------
    private Long getLoggedInPatientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new UnauthorizedException("Authentication required");
    }

    // -------------------- CRUD Methods --------------------
    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        Doctor doctor = fetchDoctorEntity(request.getDoctorId());
        Patient patient = fetchPatientEntity(request.getPatientId());

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(request.getStatus());

        appointment.setStatus(request.getStatus() != null ? request.getStatus() : AppointmentStatus.PENDING);

        // Link doctor and patient
        if (!doctor.getPatients().contains(patient)) {
            doctor.getPatients().add(patient);
            doctorRepository.save(doctor);
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment existing = fetchAppointmentEntity(id);

        Long loggedInPatientId = getLoggedInPatientId();
        if (!existing.getPatient().getId().equals(loggedInPatientId)) {
            throw new UnauthorizedException("You can only update your own appointments");
        }

        existing.setDoctor(fetchDoctorEntity(request.getDoctorId()));
        existing.setPatient(fetchPatientEntity(request.getPatientId()));
        existing.setAppointmentTime(request.getAppointmentTime());
        existing.setStatus(request.getStatus());

        return appointmentRepository.save(existing);
    }

    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment existing = fetchAppointmentEntity(id);
        existing.setStatus(status);
        return appointmentRepository.save(existing);
    }

    public Appointment updateAppointmentByAdmin(Long id, AppointmentRequest request) {
        Appointment existing = fetchAppointmentEntity(id);

        existing.setDoctor(fetchDoctorEntity(request.getDoctorId()));
        existing.setPatient(fetchPatientEntity(request.getPatientId()));
        existing.setAppointmentTime(request.getAppointmentTime());
        existing.setStatus(request.getStatus());

        return appointmentRepository.save(existing);
    }

    @Transactional
    public Appointment updateAppointmentPartial(Long id, Map<String, Object> updates, Long patientId) {
        Appointment existing = fetchAppointmentEntity(id);

        if (!existing.getPatient().getId().equals(patientId)) {
            throw new UnauthorizedException("You can only update your own appointments");
        }

        if (updates.containsKey("appointmentTime") && updates.get("appointmentTime") != null) {
            String timeStr = updates.get("appointmentTime").toString();
            existing.setAppointmentTime(LocalDateTime.parse(timeStr));
        }

        if (updates.containsKey("status") && updates.get("status") != null) {
            existing.setStatus(AppointmentStatus.valueOf(updates.get("status").toString()));
        }

        if (updates.containsKey("doctorId") && updates.get("doctorId") != null) {
            Long doctorId = Long.valueOf(updates.get("doctorId").toString());
            Doctor doctor = fetchDoctorEntity(doctorId);
            existing.setDoctor(doctor);

            if (!doctor.getPatients().contains(existing.getPatient())) {
                doctor.getPatients().add(existing.getPatient());
                doctorRepository.save(doctor);
            }
        }

        return appointmentRepository.save(existing);
    }

    public void deleteAppointment(Long id) {
        Appointment existing = fetchAppointmentEntity(id);
        Long loggedInPatientId = getLoggedInPatientId();
        if (!existing.getPatient().getId().equals(loggedInPatientId)) {
            throw new UnauthorizedException("Cannot delete another patient's appointment");
        }
        existing.setDeletedAt(LocalDateTime.now());
        appointmentRepository.save(existing);
    }

    public void deleteAppointmentByAdmin(Long id) {
        Appointment existing = fetchAppointmentEntity(id);
        existing.setDeletedAt(LocalDateTime.now());
        appointmentRepository.save(existing);
    }

    // -------------------- Fetch Entity Helpers --------------------
    private Appointment fetchAppointmentEntity(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private Doctor fetchDoctorEntity(Long doctorId) {
        return doctorRepository.findByIdAndDeletedAtIsNull(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    private Patient fetchPatientEntity(Long patientId) {
        return patientRepository.findByIdAndDeletedAtIsNull(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    // -------------------- Fetch Lists (Entities) --------------------
    public List<Appointment> getAppointmentsByPatientIdEntity(Long patientId) {
        Patient patient = fetchPatientEntity(patientId);
        return appointmentRepository.findByPatientAndDeletedAtIsNull(patient);
    }

    public List<Appointment> getAppointmentsByDoctorIdEntity(Long doctorId) {
        Doctor doctor = fetchDoctorEntity(doctorId);
        return appointmentRepository.findByDoctorAndDeletedAtIsNull(doctor);
    }

    public List<Appointment> getAllAppointmentsEntity() {
        return appointmentRepository.findByDeletedAtIsNull();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null);
    }

    // -------------------- DTO Mapping --------------------
    public AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getDeletedAt());
    }

    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        return getAppointmentsByPatientIdEntity(patientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        return getAppointmentsByDoctorIdEntity(doctorId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -------------------- Dashboard / Stats --------------------
    public long countUpcomingByPatient(Long patientId) {
        return getAppointmentsByPatientIdEntity(patientId).stream()
                .filter(a -> a.getAppointmentTime().isAfter(LocalDateTime.now()))
                .count();
    }

    public long countUpcomingByDoctor(Long doctorId) {
        return getAppointmentsByDoctorIdEntity(doctorId).stream()
                .filter(a -> a.getAppointmentTime().isAfter(LocalDateTime.now()))
                .count();
    }

    public long countPastByPatient(Long patientId) {
        return getAppointmentsByPatientIdEntity(patientId).stream()
                .filter(a -> a.getAppointmentTime().isBefore(LocalDateTime.now()))
                .count();
    }

    public int countTodaysAppointments() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return (int) getAllAppointmentsEntity().stream()
                .filter(appt -> appt.getAppointmentTime().isAfter(startOfDay)
                        && appt.getAppointmentTime().isBefore(endOfDay))
                .count();
    }

    // -------------------- Weekly Stats --------------------
    public List<String> getWeeklyLabels() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        return IntStream.range(0, 7)
                .mapToObj(i -> monday.plusDays(i).getDayOfWeek().name())
                .toList();

    }

    public List<Long> getWeeklyAppointmentsByPatient(Long patientId) {
        List<Appointment> appointments = getAppointmentsByPatientIdEntity(patientId);
        return countAppointmentsPerDay(appointments);
    }

    public List<Long> getWeeklyAppointmentsByDoctor(Long doctorId) {
        List<Appointment> appointments = getAppointmentsByDoctorIdEntity(doctorId);
        return countAppointmentsPerDay(appointments);
    }

    private List<Long> countAppointmentsPerDay(List<Appointment> appointments) {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        Map<LocalDate, Long> dailyCounts = appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getAppointmentTime().toLocalDate(), Collectors.counting()));

        List<Long> counts = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            counts.add(dailyCounts.getOrDefault(monday.plusDays(i), 0L));
        }
        return counts;
    }

    // -------------------- Recent Appointments --------------------
    public List<AppointmentResponse> getRecentAppointmentsForPatient(Long patientId, int limit) {
        return getAppointmentsByPatientIdEntity(patientId).stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
                .limit(limit)
                .map(this::mapToResponse)
                .toList();
    }

    public List<AppointmentResponse> getRecentAppointmentsForDoctor(Long doctorId, int limit) {
        return getAppointmentsByDoctorIdEntity(doctorId).stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
                .limit(limit)
                .map(this::mapToResponse)
                .toList();
    }

    public List<Appointment> getRecentAppointments(int limit) {
        return appointmentRepository.findAll().stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
                .limit(limit)
                .toList();
    }

    // -------------------- Available Slots --------------------
    public List<LocalDateTime> getAvailableSlots(Long doctorId, LocalDate date) {
        List<LocalDateTime> allSlots = new ArrayList<>();
        LocalDateTime start = date.atTime(9, 0);
        LocalDateTime end = date.atTime(17, 0);

        while (start.isBefore(end)) {
            allSlots.add(start);
            start = start.plusHours(1);
        }

        List<LocalDateTime> bookedSlots = getAppointmentsByDoctorIdEntity(doctorId).stream()
                .map(Appointment::getAppointmentTime)
                .filter(dt -> dt.toLocalDate().equals(date))
                .toList();

        allSlots.removeAll(bookedSlots);
        return allSlots;
    }
}
