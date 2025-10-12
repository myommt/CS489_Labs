package cs489.dentalsugeryapi.dentalsugeryapi.service;

 
import java.util.List;
 
import org.springframework.stereotype.Service;

import cs489.dentalsugeryapi.dentalsugeryapi.exception.PatientNotFoundException;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.repository.PatientRepository;
import cs489.dentalsugeryapi.dentalsugeryapi.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

    private PatientRepository patientRepository;
    
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient addNewPatient(Patient patient) {
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
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatientById(Integer id) {
        patientRepository.deleteById(id);
    }

}

