package cs489.dentalsugeryapi.dentalsugeryapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    
    // Method to find all patients sorted by last name in ascending order
    List<Patient> findAllByOrderByLastNameAsc();
    
    // Method to search patients by first name, last name, or email (case insensitive)
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);
    
    // Method to find all patients by address
    List<Patient> findByAddress(cs489.dentalsugeryapi.dentalsugeryapi.model.Address address);

}
