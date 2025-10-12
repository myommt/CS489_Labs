package cs489.dentalsugeryapi.dentalsugeryapi.service;

 
import java.util.List;
 
import org.springframework.stereotype.Service;

import cs489.dentalsugeryapi.dentalsugeryapi.exception.PatientNotFoundException;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Address;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.repository.PatientRepository;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AddressService addressService;
    
    public PatientServiceImpl(PatientRepository patientRepository, AddressService addressService) {
        this.patientRepository = patientRepository;
        this.addressService = addressService;
    }

    @Override
    public Patient addNewPatient(Patient patient) {
        // If patient has an address, find existing or create new address
        if (patient.getAddress() != null) {
            Address savedAddress = addressService.findOrCreateAddress(patient.getAddress());
            patient.setAddress(savedAddress);
        }
        return patientRepository.save(patient); 
    }

    @Override
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patient -> new PatientResponseDTO(
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getContactNumber(),
                        patient.getEmail(),
                        patient.getDob(),
                            (patient.getAddress() != null)?
                            new AddressResponseDTO(
                                    patient.getAddress().getAddressId(),
                                    patient.getAddress().getStreet(),
                                    patient.getAddress().getCity(),
                                    patient.getAddress().getState(),
                                    patient.getAddress().getZipcode()
                            ):null
                ))
                .toList();
    }

    @Override
    public Patient getPatientById(Integer id) throws PatientNotFoundException {
        return patientRepository.findById(id)
                .orElseThrow(()-> new PatientNotFoundException("Patient with ID " + id + " not found."));
    }

    @Override
    public Patient updatePatient(Patient patient) throws PatientNotFoundException {
        // Get the existing patient to preserve the address relationship
        Patient existingPatient = patientRepository.findById(patient.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + patient.getPatientId() + " not found."));
        
        // Update basic patient fields
        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setContactNumber(patient.getContactNumber());
        existingPatient.setEmail(patient.getEmail());
        existingPatient.setDob(patient.getDob());
        
        // Handle address update
        if (patient.getAddress() != null) {
            // If patient has new address data, find existing or create new address and link it
            Address savedAddress = addressService.findOrCreateAddress(patient.getAddress());
            existingPatient.setAddress(savedAddress);
        } else {
            // If no address provided in update, keep existing address (don't remove it)
            // existingPatient.setAddress(null); // Uncomment this line if you want to remove address when not provided
        }
        
        return patientRepository.save(existingPatient);
    }

    @Override
    public boolean deletePatientById(Integer id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<PatientResponseDTO> getAllPatientsSortedByLastName() {
        return patientRepository.findAllByOrderByLastNameAsc()
                .stream()
                .map(patient -> new PatientResponseDTO(
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getContactNumber(),
                        patient.getEmail(),
                        patient.getDob(),
                        (patient.getAddress() != null)?
                        new AddressResponseDTO(
                                patient.getAddress().getAddressId(),
                                patient.getAddress().getStreet(),
                                patient.getAddress().getCity(),
                                patient.getAddress().getState(),
                                patient.getAddress().getZipcode()
                        ):null
                ))
                .toList();
    }

    @Override
    public List<Patient> searchPatients(String searchString) {
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchString, searchString, searchString);
    }

}

