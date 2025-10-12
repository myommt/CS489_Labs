package cs489.dentalsugeryapi.dentalsugeryapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs489.dentalsugeryapi.dentalsugeryapi.dto.*;
import cs489.dentalsugeryapi.dentalsugeryapi.model.*;
import cs489.dentalsugeryapi.dentalsugeryapi.service.*;

@RestController
@RequestMapping(value = "/dentalsugery/api/appointments")
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

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<AppointmentResponseDTO> appointmentDTOs = appointments.stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(appointmentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Integer id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return ResponseEntity.ok(mapToDTO(appointment));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        try {
            Appointment appointment = mapToEntity(appointmentRequestDTO);
            Appointment createdAppointment = appointmentService.addNewAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdAppointment));
        } catch (Exception _) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Integer id, @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        try {
            // Verify the appointment exists first
            appointmentService.getAppointmentById(id);
            
            Appointment appointment = mapToEntity(appointmentRequestDTO);
            appointment.setAppointmentId(id);
            Appointment updatedAppointment = appointmentService.updateAppointment(appointment);
            return ResponseEntity.ok(mapToDTO(updatedAppointment));
        } catch (Exception _) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
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