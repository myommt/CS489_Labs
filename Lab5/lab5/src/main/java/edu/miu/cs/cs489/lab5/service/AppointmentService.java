package edu.miu.cs.cs489.lab5.service;

import edu.miu.cs.cs489.lab5.model.Appointment;
import edu.miu.cs.cs489.lab5.model.Dentist;
import edu.miu.cs.cs489.lab5.model.Patient;
import edu.miu.cs.cs489.lab5.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab5.repository.DentistRepository;
import edu.miu.cs.cs489.lab5.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DentistRepository dentistRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DentistRepository dentistRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.dentistRepository = dentistRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Appointment bookAppointment(Long patientId, Long dentistId, Long surgeryId, LocalDateTime when) {
        // Load patient and dentist
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        if (patient.isHasOutstandingBill()) {
            throw new IllegalStateException("Patient has outstanding bill and cannot book new appointments");
        }

        Dentist dentist = dentistRepository.findById(dentistId).orElseThrow(() -> new IllegalArgumentException("Dentist not found"));

        // compute week bounds (Monday start)
        LocalDateTime startOfWeek = when.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        long count = appointmentRepository.countForDentistBetween(dentist.getId(), startOfWeek, endOfWeek);
        if (count >= 5) {
            throw new IllegalStateException("Dentist already has 5 or more appointments in the requested week");
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDentist(dentist);
        // set surgery by id via JPA reference to avoid new Surgery dependency here
        // we'll set surgery to null for now; controller can set it if available
        appt.setAppointmentDateTime(when);

        return appointmentRepository.save(appt);
    }
}
