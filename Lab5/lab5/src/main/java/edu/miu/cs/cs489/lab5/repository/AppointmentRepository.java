package edu.miu.cs.cs489.lab5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.miu.cs.cs489.lab5.model.Appointment;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dentist.id = :dentistId AND a.appointmentDateTime >= :start AND a.appointmentDateTime < :end")
    long countForDentistBetween(@Param("dentistId") Long dentistId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
