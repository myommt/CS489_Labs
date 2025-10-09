package cs489.dentalsugeryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.dentalsugeryapp.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

}
