package cs489.dentalsugeryapi.dentalsugeryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    
}