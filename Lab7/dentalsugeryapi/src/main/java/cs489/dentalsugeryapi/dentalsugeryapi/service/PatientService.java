package cs489.dentalsugeryapi.dentalsugeryapi.service;

 
import cs489.dentalsugeryapi.dentalsugeryapi.exception.PatientNotFoundException;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientResponseDTO;
import java.util.List;

public interface PatientService {

    Patient addNewPatient(Patient patient);
    List<PatientResponseDTO> getAllPatients();  
    List<PatientResponseDTO> getAllPatientsSortedByLastName();
    Patient getPatientById(Integer id) throws PatientNotFoundException; 
    Patient updatePatient(Patient patient) throws PatientNotFoundException;
    boolean deletePatientById(Integer id);
    List<Patient> searchPatients(String searchString);

}

