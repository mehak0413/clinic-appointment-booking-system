package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.AppointmentRequest;
import com.clinic.appointmentservice.client.PrescriptionHistoryClient;
import com.clinic.appointmentservice.dto.AppointmentResponse;
import com.clinic.appointmentservice.dto.DoctorRequest;
import com.clinic.appointmentservice.dto.PatientRequest;
import com.clinic.appointmentservice.dto.PrescriptionHistory;
import com.clinic.appointmentservice.dto.PrescriptionRequest;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.service.DoctorService;
import com.clinic.appointmentservice.service.PatientService;
import com.clinic.appointmentservice.service.PrescriptionService;
import com.clinic.appointmentservice.entity.Appointment;
import com.clinic.appointmentservice.service.AppointmentService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ViewController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrescriptionHistoryClient prescriptionHistoryClient;

    private final AppointmentService appointmentService;

    public ViewController(AppointmentService appointmentService,
            DoctorService doctorService,
            PatientService patientService, PrescriptionService prescriptionService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.prescriptionService = prescriptionService;
    }

    // --- Index & Login ---
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // --- Registration Pages ---
    @GetMapping("/register/patient")
    public String registerPatientPage(Model model) {
        model.addAttribute("patientRequest", new PatientRequest());
        return "register-patient";
    }

    @GetMapping("/register/doctor")
    public String registerDoctorPage(Model model) {
        model.addAttribute("doctorRequest", new DoctorRequest());
        return "register-doctor";
    }

    @GetMapping("/register/admin")
    public String registerAdminPage(Model model) {
        model.addAttribute("adminRequest", new DoctorRequest());
        return "admin-dashboard";
    }

    // --- Registration Handlers ---
    @PostMapping("/register/patient")
    public String registerPatient(@ModelAttribute PatientRequest patientRequest) {
        Patient patient = new Patient();
        patient.setName(patientRequest.getName());
        patient.setEmail(patientRequest.getEmail());
        patient.setPassword(passwordEncoder.encode(patientRequest.getPassword()));
        patient.setRole("PATIENT");

        patientService.addPatientEncoded(patient);

        return "redirect:/login";
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@ModelAttribute DoctorRequest doctorRequest) {
        Doctor doctor = new Doctor();
        doctor.setName(doctorRequest.getName());
        doctor.setEmail(doctorRequest.getEmail());
        doctor.setSpecialization(doctorRequest.getSpecialization());
        doctor.setPassword(passwordEncoder.encode(doctorRequest.getPassword()));
        doctor.setRole("DOCTOR");
        doctorService.addDoctorEncoded(doctor);
        return "redirect:/login";
    }

    @PostMapping("/register/admin")
    public String registerAdmin(@ModelAttribute("adminRequest") DoctorRequest adminRequest) {
        Doctor admin = new Doctor();
        admin.setName(adminRequest.getName());
        admin.setEmail(adminRequest.getEmail());
        admin.setSpecialization(adminRequest.getSpecialization());
        admin.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
        admin.setRole("ADMIN");
        doctorService.addDoctorEncoded(admin);
        return "redirect:/login?adminRegistered";
    }

    // --- Dashboard Redirect ---
    @GetMapping("/dashboard")
    public String dashboardRedirect(HttpServletRequest request) {
        if (request.getUserPrincipal() == null)
            return "redirect:/login";

        String role = request.isUserInRole("ADMIN") ? "ADMIN"
                : request.isUserInRole("DOCTOR") ? "DOCTOR"
                        : request.isUserInRole("PATIENT") ? "PATIENT" : "";

        switch (role) {
            case "ADMIN":
                return "redirect:/admin/dashboard";
            case "DOCTOR":
                return "redirect:/doctor/dashboard";
            case "PATIENT":
                return "redirect:/patient/dashboard";
            default:
                return "redirect:/login";
        }
    }

    // --- Doctor Dashboard ---
    @GetMapping("/doctor/dashboard")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorDashboard(Model model, Principal principal) {
        // Get the logged-in doctor using emailmv
        Doctor doctor = doctorService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Long doctorId = doctor.getId();
        model.addAttribute("appointments", appointmentService.getAppointmentsByDoctorId(doctorId));
        model.addAttribute("upcomingAppointments", appointmentService.countUpcomingByDoctor(doctorId));
        model.addAttribute("totalPatients", patientService.getAllPatients().size());
        model.addAttribute("availableSlots", appointmentService.getAvailableSlots(doctorId, LocalDate.now()));
        model.addAttribute("chartLabels", appointmentService.getWeeklyLabels());
        model.addAttribute("chartData", appointmentService.getWeeklyAppointmentsByDoctor(doctorId));

        return "doctor-dashboard";
    }

    @GetMapping("/manage-prescriptions")
    @PreAuthorize("hasRole('DOCTOR')")
    public String managePrescriptionsPage(Model model, Principal principal) {
        // Find logged-in doctor
        Doctor doctor = doctorService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Fetch doctor’s appointments (DTO)
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getId());

        // Add to model
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);

        return "manage-prescriptions";
    }

    @GetMapping("/doctor/prescriptions/history")
    @PreAuthorize("hasRole('DOCTOR')")
    public String prescriptionHistoryPage(Model model, Principal principal) {
        Doctor doctor = doctorService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<PrescriptionHistory> history = prescriptionHistoryClient.getHistoryByDoctor(doctor.getId());

        model.addAttribute("doctor", doctor);
        model.addAttribute("history", history);
        return "prescription-history";
    }

    @GetMapping("/doctor/prescriptions/write/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public String showPrescriptionForm(@PathVariable Long appointmentId, Model model) {
        PrescriptionRequest prescriptionRequest = new PrescriptionRequest();
        prescriptionRequest.setAppointmentId(appointmentId);
        model.addAttribute("prescription", prescriptionRequest);
        return "write-prescription"; // a new Thymeleaf page
    }

    @PostMapping("/doctor/prescriptions/write")
    @PreAuthorize("hasRole('DOCTOR')")
    public String submitPrescription(@ModelAttribute PrescriptionRequest prescriptionRequest) {
        prescriptionService.createPrescription(prescriptionRequest);
        return "redirect:/doctor/dashboard?prescriptionCreated";
    }

    // --- Patient Dashboard ---
    @GetMapping("/patient/dashboard")
    @PreAuthorize("hasRole('PATIENT')")
    public String patientDashboard(Model model, Principal principal) {
        Patient patient = patientService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Long patientId = patient.getId();
        List<AppointmentResponse> allAppointments = appointmentService.getAppointmentsByPatientId(patientId);
        LocalDateTime now = LocalDateTime.now();

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("upcomingAppointments",
                allAppointments.stream().filter(a -> a.getAppointmentTime().isAfter(now)).toList());
        model.addAttribute("pastAppointments",
                allAppointments.stream().filter(a -> a.getAppointmentTime().isBefore(now)).toList());
        model.addAttribute("totalDoctors", doctorService.getDoctorsOnly().size());
        model.addAttribute("allDoctors", doctorService.getDoctorsOnly());
        model.addAttribute("chartLabels", appointmentService.getWeeklyLabels());
        model.addAttribute("chartData", appointmentService.getWeeklyAppointmentsByPatient(patientId));
        return "patient-dashboard";
    }

    @GetMapping("/patient/prescriptions")
    @PreAuthorize("hasRole('PATIENT')")
    public String viewMyPrescriptions(Model model, Principal principal) {
        Patient patient = patientService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Long patientId = patient.getId();

        // Fetch prescription history from microservice
        List<PrescriptionHistory> prescriptions = prescriptionHistoryClient.getHistoryByPatient(patientId);
        model.addAttribute("prescriptions", prescriptions);

        return "patient-prescriptions";
    }

    // --- Appointments ---
    @GetMapping("/appointments")
    public String showAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointmentsEntity());
        return "appointments";
    }

    @GetMapping("/appointments/book")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public String showBookingForm(Model model, Authentication authentication) {

        model.addAttribute("doctors", doctorService.getDoctorsOnly());
        model.addAttribute("appointment", new AppointmentRequest());

        String email = authentication.getName();

        if (patientService.findByEmail(email).isEmpty()) {
            // Admin case → allow selecting any patient
            model.addAttribute("patients", patientService.getAllPatients());
        } else {
            // Patient case → force self
            Patient patient = patientService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            model.addAttribute("patient", patient);
        }

        return "book-appointment";
    }

    @PostMapping("/appointments/book")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public String bookAppointment(@ModelAttribute AppointmentRequest appointmentRequest,
            Authentication authentication,
            Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();

        if (patientService.findByEmail(email).isPresent()) {
            Patient patient = patientService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            appointmentRequest.setPatientId(patient.getId());
        }

        Appointment savedAppointment = appointmentService.createAppointment(appointmentRequest);
        model.addAttribute("appointment", savedAppointment);
        return "appointment-success";
    }

    @PostMapping("/patient/appointments/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public String cancelAppointment(@PathVariable Long id, Principal principal) {
        patientService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        appointmentService.deleteAppointment(id);
        return "redirect:/patient/dashboard";
    }

    // --- Admin Dashboard + CRUD for Doctors ---
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("totalDoctors", doctorService.getDoctorsOnly().size());
        model.addAttribute("totalPatients", patientService.getAllPatients().size());
        model.addAttribute("appointmentsToday", appointmentService.countTodaysAppointments());
        model.addAttribute("recentAppointments", appointmentService.getRecentAppointments(5));
        model.addAttribute("allDoctors", doctorService.getDoctorsOnly());
        model.addAttribute("allPatients", patientService.getAllPatients());
        model.addAttribute("allAppointments", appointmentService.getAllAppointmentsEntity());
        return "admin-dashboard";
    }

    // --- Doctor Update ---

    @PostMapping("/admin/doctors/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateDoctor(@PathVariable Long id, @ModelAttribute DoctorRequest doctorRequest) {
        doctorService.updateDoctor(id, doctorRequest);
        return "redirect:/admin/dashboard";
    }

    // --- Doctor Delete ---
    @PostMapping("/admin/doctors/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/admin/dashboard";
    }
}
