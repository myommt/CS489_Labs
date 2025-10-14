package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        model.addAttribute("pageTitle", "Appointment List");
        return "secured/appointment/list";
    }

    @GetMapping("/secured/appointment/new")
    public String showNewAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        populateReferenceData(model);
        model.addAttribute("pageTitle", "Add New Appointment");
        return "secured/appointment/new";
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
            model.addAttribute("pageTitle", "Add New Appointment");
            return "secured/appointment/new";
        }
        try {
            // Attach associations
            appointment.setPatient(patientService.getPatientById(patientId));
            appointment.setDentist(dentistService.findDentistById(dentistId).orElseThrow());
            appointment.setSurgeryLocation(surgeryLocationService.findSurgeryLocationById(surgeryLocationId).orElseThrow());

            Appointment created = appointmentService.addNewAppointment(appointment);
            ra.addFlashAttribute("successMessage", "Appointment #" + created.getAppointmentId() + " created successfully.");
            return "redirect:/secured/appointment/list";
        } catch (OutstandingBillException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (AppointmentLimitExceededException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating appointment: " + e.getMessage());
        }
        populateReferenceData(model);
        model.addAttribute("pageTitle", "Add New Appointment");
        return "secured/appointment/new";
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
        } catch (OutstandingBillException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (AppointmentLimitExceededException e) {
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
        appointment.setAppointmentType(dto.appointmentType());
        appointment.setAppointmentStatus(dto.appointmentStatus());
        appointment.setAppointmentDateTime(dto.appointmentDateTime());

        // Create and set Patient
        if (dto.patientRequestDTO() != null) {
            Patient patient = new Patient();
            patient.setFirstName(dto.patientRequestDTO().firstName());
            patient.setLastName(dto.patientRequestDTO().lastName());
            patient.setContactNumber(dto.patientRequestDTO().contactNumber());
            patient.setEmail(dto.patientRequestDTO().email());
            patient.setDob(dto.patientRequestDTO().dob());
            
            // Create and set Address for Patient
            if (dto.patientRequestDTO().addressRequestDTO() != null) {
                Address address = new Address();
                address.setStreet(dto.patientRequestDTO().addressRequestDTO().street());
                address.setCity(dto.patientRequestDTO().addressRequestDTO().city());
                address.setState(dto.patientRequestDTO().addressRequestDTO().state());
                address.setZipcode(dto.patientRequestDTO().addressRequestDTO().zipcode());
                address = addressService.findOrCreateAddress(address);
                patient.setAddress(address);
            }
            
            patient = patientService.addNewPatient(patient);
            appointment.setPatient(patient);
        }

        // Create and set Dentist
        if (dto.dentistRequestDTO() != null) {
            Dentist dentist = new Dentist();
            dentist.setFirstName(dto.dentistRequestDTO().firstName());
            dentist.setLastName(dto.dentistRequestDTO().lastName());
            dentist.setContactNumber(dto.dentistRequestDTO().contactNumber());
            dentist.setEmail(dto.dentistRequestDTO().email());
            dentist.setSpecialization(dto.dentistRequestDTO().specialization());
            
            dentist = dentistService.saveDentist(dentist);
            appointment.setDentist(dentist);
        }

        // Create and set SurgeryLocation
        if (dto.surgeryLocationRequestDTO() != null) {
            SurgeryLocation location = new SurgeryLocation();
            location.setName(dto.surgeryLocationRequestDTO().name());
            location.setContactNumber(dto.surgeryLocationRequestDTO().contactNumber());
            
            // Create and set Address for SurgeryLocation
            if (dto.surgeryLocationRequestDTO().addressRequestDTO() != null) {
                Address address = new Address();
                address.setStreet(dto.surgeryLocationRequestDTO().addressRequestDTO().street());
                address.setCity(dto.surgeryLocationRequestDTO().addressRequestDTO().city());
                address.setState(dto.surgeryLocationRequestDTO().addressRequestDTO().state());
                address.setZipcode(dto.surgeryLocationRequestDTO().addressRequestDTO().zipcode());
                address = addressService.findOrCreateAddress(address);
                location.setLocation(address);
            }
            
            location = surgeryLocationService.saveSurgeryLocation(location);
            appointment.setSurgeryLocation(location);
        }

        return appointment;
    }
}
