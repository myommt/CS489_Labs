package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.*;
import cs489.miu.dentalsurgeryapp.model.*;
import cs489.miu.dentalsurgeryapp.service.*;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;

@Controller("appointmentController")
public class AppointmentController {

    // Constants for repeated strings
    private static final String PAGE_TITLE = "pageTitle";
    private static final String APPOINTMENT = "appointment";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String REDIRECT_APPOINTMENT_LIST = "redirect:/secured/appointment/list";
    private static final String APPOINTMENT_NEW_VIEW = "secured/appointment/new";
    private static final String APPOINTMENT_EDIT_VIEW = "secured/appointment/edit";
    private static final String ADD_NEW_APPOINTMENT_TITLE = "Add New Appointment";
    private static final String EDIT_APPOINTMENT_TITLE = "Edit Appointment";
    private static final String APPOINTMENT_NOT_FOUND_MSG = "Appointment not found with ID: ";

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DentistService dentistService;
    private final SurgeryLocationService surgeryLocationService;
    private final AddressService addressService;

    public AppointmentController(AppointmentService appointmentService, 
                               PatientService patientService,
                               DentistService dentistService,
                               SurgeryLocationService surgeryLocationService,
                               AddressService addressService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.surgeryLocationService = surgeryLocationService;
        this.addressService = addressService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================
    @GetMapping({"/secured/appointment/", "/secured/appointment/list"})
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        model.addAttribute(PAGE_TITLE, "Appointment List");
        return "secured/appointment/list";
    }

    @GetMapping("/secured/appointment/my-appointments")
    public String listMyAppointments(Model model, Authentication authentication,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "appointmentDateTime") String sortBy,
                                    @RequestParam(defaultValue = "asc") String sortDir,
                                    @RequestParam(required = false) String status) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        
        // Check if user is a dentist
        boolean isDentist = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DENTIST"));
        
        if (isDentist && currentUser.getDentist() != null) {
            // Redirect to dentist's role-based appointments page with proper data
            Dentist dentist = currentUser.getDentist();
            
            // Create pageable with sorting
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Get appointments for this dentist
            Page<Appointment> appointments;
            if (status != null && !status.isEmpty()) {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointmentService.findAppointmentsByDentistAndStatus(dentist, appointmentStatus, pageable);
            } else {
                appointments = appointmentService.findAppointmentsByDentist(dentist, pageable);
            }
            
            // Add model attributes expected by dentist appointments template
            model.addAttribute("dentist", dentist);
            model.addAttribute("appointments", appointments);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("appointmentStatuses", AppointmentStatus.values());
            
            return "rolebase/dentist/appointments";
        } else {
            // For non-dentist users, redirect to dashboard or show error
            return "redirect:/secured/dashboard";
        }
    }

    @GetMapping("/secured/patient/history")
    public String patientHistory(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        
        // Check if user is a dentist
        boolean isDentist = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DENTIST"));
        
        if (isDentist) {
            // Redirect to dentist dashboard
            return "redirect:/dentalsurgeryapp/rolebase/dentist/dashboard";
        } else {
            // For other users, redirect to main dashboard
            return "redirect:/secured/dashboard";
        }
    }

    @GetMapping("/secured/patient/appointments/new")
    public String patientAppointmentsNew(Authentication authentication) {
        // This route is being removed as requested
        // Redirect users to appropriate dashboard based on their role
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        
        // Check user roles and redirect appropriately
        boolean isDentist = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DENTIST"));
        boolean isPatient = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PATIENT"));
        
        if (isDentist) {
            return "redirect:/dentalsurgeryapp/rolebase/dentist/dashboard";
        } else if (isPatient) {
            return "redirect:/dentalsurgeryapp/rolebase/patient/dashboard";
        } else {
            return "redirect:/secured/dashboard";
        }
    }

    @GetMapping("/secured/appointment/new")
    public String showNewAppointmentForm(Model model) {
        model.addAttribute(APPOINTMENT, new Appointment());
        populateReferenceData(model);
        model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
        return APPOINTMENT_NEW_VIEW;
    }

    @PostMapping("/secured/appointment/new")
    public String createAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    @RequestParam Integer patientId,
                                    @RequestParam Integer dentistId,
                                    @RequestParam Integer surgeryLocationId,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            populateReferenceData(model);
            model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
            return APPOINTMENT_NEW_VIEW;
        }
        try {
            // Attach associations
            appointment.setPatient(patientService.getPatientById(patientId));
            appointment.setDentist(dentistService.findDentistById(dentistId).orElseThrow());
            appointment.setSurgeryLocation(surgeryLocationService.findSurgeryLocationById(surgeryLocationId).orElseThrow());

            Appointment created = appointmentService.addNewAppointment(appointment);
            ra.addFlashAttribute(SUCCESS_MESSAGE, "Appointment #" + created.getAppointmentId() + " created successfully.");
            return REDIRECT_APPOINTMENT_LIST;
        } catch (OutstandingBillException | AppointmentLimitExceededException e) {
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE, "Error creating appointment: " + e.getMessage());
        }
        populateReferenceData(model);
        model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
        return APPOINTMENT_NEW_VIEW;
    }

    @GetMapping("/secured/appointment/edit/{id}")
    public String showEditAppointmentForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Appointment appt = appointmentService.getAppointmentById(id);
        if (appt == null) {
            ra.addFlashAttribute("errorMessage", "Appointment not found with ID: " + id);
            return "redirect:/secured/appointment/list";
        }
        model.addAttribute("appointment", appt);
        populateReferenceData(model);
        model.addAttribute("pageTitle", "Edit Appointment");
        return "secured/appointment/edit";
    }

    @PostMapping("/secured/appointment/edit/{id}")
    public String updateAppointment(@PathVariable Integer id,
                                    @ModelAttribute("appointment") Appointment appointment,
                                    @RequestParam Integer patientId,
                                    @RequestParam Integer dentistId,
                                    @RequestParam Integer surgeryLocationId,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            populateReferenceData(model);
            model.addAttribute("pageTitle", "Edit Appointment");
            return "secured/appointment/edit";
        }
        try {
            appointment.setAppointmentId(id);
            appointment.setPatient(patientService.getPatientById(patientId));
            appointment.setDentist(dentistService.findDentistById(dentistId).orElseThrow());
            appointment.setSurgeryLocation(surgeryLocationService.findSurgeryLocationById(surgeryLocationId).orElseThrow());
            Appointment updated = appointmentService.updateAppointment(appointment);
            ra.addFlashAttribute("successMessage", "Appointment #" + updated.getAppointmentId() + " updated successfully.");
            return "redirect:/secured/appointment/list";
        } catch (OutstandingBillException | AppointmentLimitExceededException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating appointment: " + e.getMessage());
        }
        populateReferenceData(model);
        model.addAttribute("pageTitle", "Edit Appointment");
        return "secured/appointment/edit";
    }

    @GetMapping("/secured/appointment/view/{id}")
    public String viewAppointment(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Appointment appt = appointmentService.getAppointmentById(id);
        if (appt == null) {
            ra.addFlashAttribute("errorMessage", "Appointment not found with ID: " + id);
            return "redirect:/secured/appointment/list";
        }
        model.addAttribute("appointment", appt);
        model.addAttribute("pageTitle", "Appointment Details");
        return "secured/appointment/view";
    }

    @PostMapping("/secured/appointment/delete/{id}")
    public String deleteAppointmentUi(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            boolean deleted = appointmentService.deleteAppointmentById(id);
            if (deleted) {
                ra.addFlashAttribute("successMessage", "Appointment deleted successfully.");
            } else {
                ra.addFlashAttribute("errorMessage", "Appointment not found with ID: " + id);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error deleting appointment: " + e.getMessage());
        }
        return "redirect:/secured/appointment/list";
    }

    private void populateReferenceData(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        // Prefer ordered lists for better UX
        model.addAttribute("dentists", dentistService.getAllDentistsOrderedByName());
        model.addAttribute("surgeryLocations", surgeryLocationService.getAllSurgeryLocationsOrderedByName());
        // Back-compat aliases and static lists (if templates use them)
        model.addAttribute("locations", surgeryLocationService.getAllSurgeryLocationsOrderedByName());
        model.addAttribute("statuses", java.util.List.of("SCHEDULED", "COMPLETED", "CANCELLED"));
        model.addAttribute("types", java.util.List.of("CHECKUP", "CLEANING", "FILLING", "SURGERY"));
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<AppointmentResponseDTO> appointmentDTOs = appointments.stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(appointmentDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/appointments/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Integer id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return ResponseEntity.ok(mapToDTO(appointment));
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/appointments")
    public ResponseEntity<Object> createAppointment(@RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        try {
            Appointment appointment = mapToEntity(appointmentRequestDTO);
            Appointment createdAppointment = appointmentService.addNewAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdAppointment));
        } catch (OutstandingBillException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Outstanding Bills", e.getMessage()));
        } catch (AppointmentLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Appointment Limit Exceeded", e.getMessage()));
        } catch (Exception _) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Bad Request", "Invalid appointment data provided"));
        }
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/appointments/{id}")
    public ResponseEntity<Object> updateAppointment(@PathVariable Integer id, @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        try {
            // Verify the appointment exists first
            appointmentService.getAppointmentById(id);
            
            Appointment appointment = mapToEntity(appointmentRequestDTO);
            appointment.setAppointmentId(id);
            Appointment updatedAppointment = appointmentService.updateAppointment(appointment);
            return ResponseEntity.ok(mapToDTO(updatedAppointment));
        } catch (OutstandingBillException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Outstanding Bills", e.getMessage()));
        } catch (AppointmentLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Appointment Limit Exceeded", e.getMessage()));
        } catch (Exception _) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Bad Request", "Invalid appointment data or appointment not found"));
        }
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/appointments/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteAppointment(@PathVariable Integer id) {
        boolean deleted = appointmentService.deleteAppointmentById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Appointment with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Appointment with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private AppointmentResponseDTO mapToDTO(Appointment appointment) {
        // Map Patient to PatientResponseDTO
        PatientResponseDTO patientResponseDTO = null;
        if (appointment.getPatient() != null) {
            Patient patient = appointment.getPatient();
            AddressResponseDTO addressDTO = null;
            if (patient.getAddress() != null) {
                Address address = patient.getAddress();
                addressDTO = new AddressResponseDTO(
                        address.getAddressId(),
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode()
                );
            }
            patientResponseDTO = new PatientResponseDTO(
                    patient.getPatientId(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    patient.getContactNumber(),
                    patient.getEmail(),
                    patient.getDob(),
                    addressDTO
            );
        }

        // Map Dentist to DentistResponseDTO
        DentistResponseDTO dentistResponseDTO = null;
        if (appointment.getDentist() != null) {
            Dentist dentist = appointment.getDentist();
            dentistResponseDTO = new DentistResponseDTO(
                    dentist.getDentistId(),
                    dentist.getFirstName(),
                    dentist.getLastName(),
                    dentist.getContactNumber(),
                    dentist.getEmail(),
                    dentist.getSpecialization()
            );
        }

        // Map SurgeryLocation to SurgeryLocationResponseDTO
        SurgeryLocationResponseDTO surgeryLocationResponseDTO = null;
        if (appointment.getSurgeryLocation() != null) {
            SurgeryLocation location = appointment.getSurgeryLocation();
            AddressResponseDTO locationAddressDTO = null;
            if (location.getLocation() != null) {
                Address address = location.getLocation();
                locationAddressDTO = new AddressResponseDTO(
                        address.getAddressId(),
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode()
                );
            }
            surgeryLocationResponseDTO = new SurgeryLocationResponseDTO(
                    location.getSurgeryLocationId(),
                    location.getName(),
                    location.getContactNumber(),
                    locationAddressDTO
            );
        }

        return new AppointmentResponseDTO(
                appointment.getAppointmentId(),
                appointment.getAppointmentType(),
                appointment.getAppointmentStatus(),
                appointment.getAppointmentDateTime(),
                patientResponseDTO,
                dentistResponseDTO,
                surgeryLocationResponseDTO
        );
    }

    private Appointment mapToEntity(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentType(dto.getAppointmentType());
        appointment.setAppointmentStatus(dto.getAppointmentStatus());
        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());

        // Create and set Patient
        if (dto.getPatientRequestDTO() != null) {
            Patient patient = new Patient();
            patient.setFirstName(dto.getPatientRequestDTO().firstName());
            patient.setLastName(dto.getPatientRequestDTO().lastName());
            patient.setContactNumber(dto.getPatientRequestDTO().contactNumber());
            patient.setEmail(dto.getPatientRequestDTO().email());
            patient.setDob(dto.getPatientRequestDTO().dob());
            
            // Create and set Address for Patient
            if (dto.getPatientRequestDTO().addressRequestDTO() != null) {
                Address address = new Address();
                address.setStreet(dto.getPatientRequestDTO().addressRequestDTO().street());
                address.setCity(dto.getPatientRequestDTO().addressRequestDTO().city());
                address.setState(dto.getPatientRequestDTO().addressRequestDTO().state());
                address.setZipcode(dto.getPatientRequestDTO().addressRequestDTO().zipcode());
                address = addressService.findOrCreateAddress(address);
                patient.setAddress(address);
            }
            
            patient = patientService.addNewPatient(patient);
            appointment.setPatient(patient);
        }

        // Create and set Dentist
        if (dto.getDentistRequestDTO() != null) {
            Dentist dentist = new Dentist();
            dentist.setFirstName(dto.getDentistRequestDTO().firstName());
            dentist.setLastName(dto.getDentistRequestDTO().lastName());
            dentist.setContactNumber(dto.getDentistRequestDTO().contactNumber());
            dentist.setEmail(dto.getDentistRequestDTO().email());
            dentist.setSpecialization(dto.getDentistRequestDTO().specialization());
            
            dentist = dentistService.saveDentist(dentist);
            appointment.setDentist(dentist);
        }

        // Create and set SurgeryLocation
        if (dto.getSurgeryLocationRequestDTO() != null) {
            SurgeryLocation location = new SurgeryLocation();
            location.setName(dto.getSurgeryLocationRequestDTO().name());
            location.setContactNumber(dto.getSurgeryLocationRequestDTO().contactNumber());
            
            // Create and set Address for SurgeryLocation
            if (dto.getSurgeryLocationRequestDTO().addressRequestDTO() != null) {
                Address address = new Address();
                address.setStreet(dto.getSurgeryLocationRequestDTO().addressRequestDTO().street());
                address.setCity(dto.getSurgeryLocationRequestDTO().addressRequestDTO().city());
                address.setState(dto.getSurgeryLocationRequestDTO().addressRequestDTO().state());
                address.setZipcode(dto.getSurgeryLocationRequestDTO().addressRequestDTO().zipcode());
                address = addressService.findOrCreateAddress(address);
                location.setLocation(address);
            }
            
            location = surgeryLocationService.saveSurgeryLocation(location);
            appointment.setSurgeryLocation(location);
        }

        return appointment;
    }
}
