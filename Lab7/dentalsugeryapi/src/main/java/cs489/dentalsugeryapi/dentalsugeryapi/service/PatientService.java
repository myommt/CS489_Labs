package cs489.dentalsugeryapi.dentalsugeryapi.service;

 
import cs489.dentalsugeryapi.dentalsugeryapi.exception.PatientNotFoundException;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import java.util.List;

public interface PatientService {

    Patient addNewPatient(Patient patient);
    List<Patient> getAllPatients();  
    Patient getPatientById(Integer id) throws PatientNotFoundException; 
    Patient updatePatient(Patient patient);
    void deletePatientById(Integer id);

}

