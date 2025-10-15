package cs489.miu.dentalsurgeryapp.controller.rolebase.patient;

import cs489.miu.dentalsurgeryapp.dto.AppointmentRequestDTO;
import cs489.miu.dentalsurgeryapp.dto.request.UserUpdateRequestDTO;
import cs489.miu.dentalsurgeryapp.dto.response.UserResponseDTO;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.AppointmentStatus;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.service.AppointmentService;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Controller("patientRolebaseController")
@RequestMapping("/dentalsurgeryapp/rolebase/patient")
@PreAuthorize("hasAuthority('PATIENT')")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DentistService dentistService;

    /**
     * Patient Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        // Get recent appointments
        Pageable pageable = PageRequest.of(0, 5, Sort.by("appointmentDate").descending());
        Page<Appointment> recentAppointments = appointmentService.findAppointmentsByPatient(currentPatient, pageable);

        // Get appointment counts
        long totalAppointments = appointmentService.countAppointmentsByPatient(currentPatient);
        long upcomingAppointments = appointmentService.countUpcomingAppointmentsByPatient(currentPatient);
        long completedAppointments = appointmentService.countCompletedAppointmentsByPatient(currentPatient);

        model.addAttribute("patient", currentPatient);
        model.addAttribute("recentAppointments", recentAppointments.getContent());
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("completedAppointments", completedAppointments);

        return "rolebase/patient/dashboard";
    }

    /**
     * Patient Profile - View
     */
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        // Get current user from authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setUserId(currentUser.getUserId());
        userDto.setUsername(currentUser.getUsername());
        userDto.setEmail(currentUser.getEmail());
        userDto.setFirstName(currentUser.getFirstName());
        userDto.setLastName(currentUser.getLastName());

        model.addAttribute("patient", currentPatient);
        model.addAttribute("user", userDto);

        return "rolebase/patient/profile";
    }

    /**
     * Patient Profile - Edit
     */
    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("user") UserUpdateRequestDTO userDto,
                              BindingResult bindingResult,
                              @RequestParam("dob") String dob,
                              @RequestParam("contactNumber") String contactNumber,
                              @RequestParam("address") String address,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", currentPatient);
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "rolebase/patient/profile";
        }

        try {
            // Update user information
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            userService.updateUser(user);

            // Update patient-specific information
            if (dob != null && !dob.isEmpty()) {
                currentPatient.setDob(LocalDate.parse(dob));
            }
            currentPatient.setContactNumber(contactNumber);
            // Note: Address would need proper handling with Address entity
            patientService.updatePatient(currentPatient);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/dentalsurgeryapp/rolebase/patient/profile";

        } catch (Exception e) {
            model.addAttribute("patient", currentPatient);
            model.addAttribute("user", userDto);
            model.addAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "rolebase/patient/profile";
        }
    }

    /**
     * Patient Appointments - List
     */
    @GetMapping("/appointments")
    public String listAppointments(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "appointmentDateTime") String sortBy,
                                   @RequestParam(defaultValue = "desc") String sortDir,
                                   @RequestParam(required = false) String status,
                                   Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Appointment> appointments;

        if (status != null && !status.isEmpty()) {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointments = appointmentService.findAppointmentsByPatientAndStatus(currentPatient, appointmentStatus, pageable);
        } else {
            appointments = appointmentService.findAppointmentsByPatient(currentPatient, pageable);
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentStatus", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "rolebase/patient/appointments";
    }

    /**
     * New Appointment - Form
     */
    @GetMapping("/appointments/new")
    public String newAppointmentForm(Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        AppointmentRequestDTO appointmentDto = new AppointmentRequestDTO();
        appointmentDto.setAppointmentType("ONLINE");
        
        List<Dentist> dentists = dentistService.findAllDentists();
        
        model.addAttribute("appointment", appointmentDto);
        model.addAttribute("dentists", dentists);

        return "rolebase/patient/appointment-new";
    }

    /**
     * New Appointment - Submit
     */
    @PostMapping("/appointments/new")
    public String createAppointment(@Valid @ModelAttribute("appointment") AppointmentRequestDTO appointmentDto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            List<Dentist> dentists = dentistService.findAllDentists();
            model.addAttribute("dentists", dentists);
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "rolebase/patient/appointment-new";
        }

        try {
            // Set patient and default values
            appointmentDto.setPatientId(currentPatient.getPatientId().longValue());
            appointmentDto.setStatus(AppointmentStatus.PENDING);
            appointmentDto.setAppointmentType("ONLINE");

            Appointment appointment = appointmentService.createAppointment(appointmentDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Appointment booked successfully! Reference ID: " + appointment.getAppointmentId());
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";

        } catch (AppointmentLimitExceededException | OutstandingBillException e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            model.addAttribute("dentists", dentists);
            model.addAttribute("errorMessage", e.getMessage());
            return "rolebase/patient/appointment-new";
        } catch (Exception e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            model.addAttribute("dentists", dentists);
            model.addAttribute("errorMessage", "Error booking appointment: " + e.getMessage());
            return "rolebase/patient/appointment-new";
        }
    }

    /**
     * Edit Appointment - Form
     */
    @GetMapping("/appointments/{id}/edit")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            return "redirect:/patient/appointments";
        }

        Appointment appointment = appointmentOpt.get();
        
        // Check if appointment belongs to current patient
        if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
            return "redirect:/patient/appointments";
        }

        List<Dentist> dentists = dentistService.findAllDentists();
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("dentists", dentists);

        return "rolebase/patient/appointment-edit";
    }

    /**
     * Edit Appointment - Submit
     */
    @PostMapping("/appointments/{id}/edit")
    public String updateAppointment(@PathVariable Long id,
                                    @Valid @ModelAttribute("appointment") AppointmentRequestDTO appointmentDto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            return "redirect:/patient/appointments";
        }

        Appointment appointment = appointmentOpt.get();
        
        // Check if appointment belongs to current patient
        if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
            return "redirect:/patient/appointments";
        }

        if (bindingResult.hasErrors()) {
            List<Dentist> dentists = dentistService.findAllDentists();
            model.addAttribute("appointment", appointment);
            model.addAttribute("dentists", dentists);
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "rolebase/patient/appointment-edit";
        }

        try {
            appointmentDto.setAppointmentId(id);
            appointmentDto.setPatientId(currentPatient.getPatientId().longValue());
            
            appointmentService.updateAppointment(appointmentDto);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully!");
            return "redirect:/patient/appointments";

        } catch (Exception e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            model.addAttribute("appointment", appointment);
            model.addAttribute("dentists", dentists);
            model.addAttribute("errorMessage", "Error updating appointment: " + e.getMessage());
            return "rolebase/patient/appointment-edit";
        }
    }

    /**
     * Cancel Appointment
     */
    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        try {
            Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
            if (appointmentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found");
                return "redirect:/patient/appointments";
            }

            Appointment appointment = appointmentOpt.get();
            
            // Check if appointment belongs to current patient
            if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
                return "redirect:/patient/appointments";
            }

            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED.name());
            appointmentService.saveAppointment(appointment);
            
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling appointment: " + e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    /**
     * Get current authenticated patient
     */
    private Patient getCurrentPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);
        
        if (userOpt.isEmpty()) {
            return null;
        }

        // Get patient directly from user
        return userOpt.get().getPatient();
    }
}