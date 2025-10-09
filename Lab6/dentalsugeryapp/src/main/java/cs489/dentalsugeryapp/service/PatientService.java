package cs489.dentalsugeryapp.service;

import cs489.dentalsugeryapp.exception.PatientNotFoundException;
import cs489.dentalsugeryapp.model.Patient;
import java.util.List;

public interface PatientService {

    Patient addNewPatient(Patient patient);
    List<Patient> getAllPatients();  
    Patient getPatientById(Integer id) throws PatientNotFoundException; 
    Patient updatePatient(Patient patient);
    void deletePatientById(Integer id);

}
