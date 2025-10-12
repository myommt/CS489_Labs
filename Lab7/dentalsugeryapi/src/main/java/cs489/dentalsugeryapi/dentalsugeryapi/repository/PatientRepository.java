package cs489.dentalsugeryapi.dentalsugeryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

}
