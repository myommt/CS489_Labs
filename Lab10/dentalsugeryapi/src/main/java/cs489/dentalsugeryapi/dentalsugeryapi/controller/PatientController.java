package cs489.dentalsugeryapi.dentalsugeryapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Address;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressRequestDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientRequestDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.DeleteResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.service.PatientService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import cs489.dentalsugeryapi.dentalsugeryapi.exception.PatientNotFoundException;


@RestController
@RequestMapping(value = { "/dentalsugery/api/patients" })
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody PatientRequestDTO patientRequestDTO) {
        Patient patient = mapToEntity(patientRequestDTO);
        Patient createdPatient = patientService.addNewPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdPatient));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Integer id) {
        try {
            Patient patient = patientService.getPatientById(id);
            return ResponseEntity.ok(mapToDTO(patient));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Integer id, @RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            // Convert DTO to entity and set the ID from the path
            Patient patient = mapToEntity(patientRequestDTO);
            patient.setPatientId(id);
            
            Patient updatedPatient = patientService.updatePatient(patient);
            return ResponseEntity.ok(mapToDTO(updatedPatient));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDTO> deletePatient(@PathVariable Integer id) {
        boolean deleted = patientService.deletePatientById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Patient with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Patient with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(@PathVariable String searchString) {
        List<Patient> patients = patientService.searchPatients(searchString);
        List<PatientResponseDTO> patientDTOs = patients.stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(patientDTOs);
    }

    private PatientResponseDTO mapToDTO(Patient patient) {
        AddressResponseDTO addressDTO = null;
        if (patient.getAddress() != null) {
            addressDTO = new AddressResponseDTO(
                    patient.getAddress().getAddressId(),
                    patient.getAddress().getStreet(),
                    patient.getAddress().getCity(),
                    patient.getAddress().getState(),
                    patient.getAddress().getZipcode()
            );
        }

        return new PatientResponseDTO(
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getContactNumber(),
                patient.getEmail(),
                patient.getDob(),
                addressDTO
        );
    }

    private Patient mapToEntity(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientRequestDTO.firstName());
        patient.setLastName(patientRequestDTO.lastName());
        patient.setContactNumber(patientRequestDTO.contactNumber());
        patient.setEmail(patientRequestDTO.email());
        patient.setDob(patientRequestDTO.dob());
        
        // Only create address if addressRequestDTO exists and all required fields are non-blank
        if (patientRequestDTO.addressRequestDTO() != null) {
            var addressDTO = patientRequestDTO.addressRequestDTO();
            if (isValidAddressData(addressDTO)) {
                Address address = new Address();
                address.setStreet(addressDTO.street());
                address.setCity(addressDTO.city());
                address.setState(addressDTO.state());
                address.setZipcode(addressDTO.zipcode());
                patient.setAddress(address);
            }
        }
        
        return patient;
    }
    
    private boolean isValidAddressData(AddressRequestDTO addressDTO) {
        return addressDTO.street() != null && !addressDTO.street().trim().isEmpty() &&
               addressDTO.city() != null && !addressDTO.city().trim().isEmpty() &&
               addressDTO.state() != null && !addressDTO.state().trim().isEmpty() &&
               addressDTO.zipcode() != null && !addressDTO.zipcode().trim().isEmpty();
    }

}