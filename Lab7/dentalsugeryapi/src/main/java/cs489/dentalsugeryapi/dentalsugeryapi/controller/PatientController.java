package cs489.dentalsugeryapi.dentalsugeryapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
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

    @GetMapping(value ="")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody Patient patient) {
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
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Integer id, @RequestBody Patient patient) {
        try {
            // Verify the patient exists first
            patientService.getPatientById(id);
            
            // Set the ID from the path to ensure we're updating the correct patient
            patient.setPatientId(id);
            
            Patient updatedPatient = patientService.updatePatient(patient);
            return ResponseEntity.ok(mapToDTO(updatedPatient));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        try {
            // Verify the patient exists first
            patientService.getPatientById(id);
            
            patientService.deletePatientById(id);
            return ResponseEntity.noContent().build();
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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

}