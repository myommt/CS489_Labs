package cs489.dentalsugeryapi.dentalsugeryapi.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Appointment;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Dentist;
import cs489.dentalsugeryapi.dentalsugeryapi.model.SurgeryLocation;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    
    // Find appointment by patient, dentist, datetime, and surgery location (to avoid duplicates)
    Appointment findByPatientAndDentistAndAppointmentDateTimeAndSurgeryLocation(
        Patient patient,
        Dentist dentist,
        LocalDateTime appointmentDateTime,
        SurgeryLocation surgeryLocation
    );
    
    // Count appointments for a dentist within a date range (for weekly limit validation)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dentist = :dentist AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
    long countByDentistAndAppointmentDateTimeBetween(
        @Param("dentist") Dentist dentist,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
}