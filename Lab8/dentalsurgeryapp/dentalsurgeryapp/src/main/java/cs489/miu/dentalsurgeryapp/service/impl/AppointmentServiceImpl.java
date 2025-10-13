package cs489.miu.dentalsurgeryapp.service.impl;


import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;

import org.springframework.stereotype.Service;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;
import cs489.miu.dentalsurgeryapp.repository.AppointmentRepository;
import cs489.miu.dentalsurgeryapp.service.AppointmentService;
import cs489.miu.dentalsurgeryapp.service.BillService;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.service.SurgeryLocationService;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DentistService dentistService;
    private final SurgeryLocationService surgeryLocationService;
    private final BillService billService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, 
                                 PatientService patientService,
                                 DentistService dentistService,
                                 SurgeryLocationService surgeryLocationService,
                                 BillService billService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.surgeryLocationService = surgeryLocationService;
        this.billService = billService;
    }

    @Override
    public Appointment addNewAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException {
        // Use findOrCreate to prevent duplicates and handle all entity relationships
        return findOrCreateAppointment(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Integer id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public Appointment updateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException {
        // Validate outstanding bills and weekly limit before updating
        validatePatientOutstandingBills(appointment);
        validateDentistWeeklyLimit(appointment);
        return appointmentRepository.save(appointment);
    }

    @Override
    public boolean deleteAppointmentById(Integer id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Appointment findOrCreateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException {
        // Use findOrCreate for related entities first
        if (appointment.getPatient() != null) {
            Patient managedPatient = 
                patientService.findOrCreatePatient(appointment.getPatient());
            appointment.setPatient(managedPatient);
        }
        
        if (appointment.getDentist() != null) {
            Dentist managedDentist = 
                dentistService.findOrCreateDentist(appointment.getDentist());
            appointment.setDentist(managedDentist);
        }
        
        if (appointment.getSurgeryLocation() != null) {
            SurgeryLocation managedLocation = 
                surgeryLocationService.findOrCreateSurgeryLocation(appointment.getSurgeryLocation());
            appointment.setSurgeryLocation(managedLocation);
        }
        
        // Check if appointment already exists
        if (appointment.getPatient() != null && appointment.getDentist() != null 
            && appointment.getAppointmentDateTime() != null && appointment.getSurgeryLocation() != null) {
            
            Appointment existingAppointment = appointmentRepository.findByPatientAndDentistAndAppointmentDateTimeAndSurgeryLocation(
                appointment.getPatient(), 
                appointment.getDentist(), 
                appointment.getAppointmentDateTime(), 
                appointment.getSurgeryLocation()
            );
            
            if (existingAppointment != null) {
                return existingAppointment;
            }
        }
        
        // Validate patient outstanding bills before creating new appointment
        validatePatientOutstandingBills(appointment);
        
        // Validate dentist weekly appointment limit before creating new appointment
        validateDentistWeeklyLimit(appointment);
        
        // Appointment doesn't exist, create new one
        return appointmentRepository.save(appointment);
    }

    /**
     * Validates that a dentist doesn't exceed 5 appointments per week
     * @param appointment The appointment being created/updated
     * @throws AppointmentLimitExceededException if the weekly limit would be exceeded
     */
    private void validateDentistWeeklyLimit(Appointment appointment) throws AppointmentLimitExceededException {
        if (appointment.getDentist() == null || appointment.getAppointmentDateTime() == null) {
            return; // Cannot validate without dentist and appointment date
        }
        
        // Calculate the start and end of the week for the appointment date
        LocalDateTime appointmentDate = appointment.getAppointmentDateTime();
        LocalDateTime weekStart = appointmentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                                 .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime weekEnd = appointmentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                                               .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        // Count existing appointments for this dentist in the same week
        long appointmentsInWeek = appointmentRepository.countByDentistAndAppointmentDateTimeBetween(
            appointment.getDentist(), weekStart, weekEnd);
        
        // If updating an existing appointment, don't count the current appointment
        if (appointment.getAppointmentId() != null) {
            // This is an update, so the existing appointment should not be counted twice
            appointmentsInWeek--;
        }
        
        // Check if adding this appointment would exceed the limit
        if (appointmentsInWeek >= 5) {
            throw new AppointmentLimitExceededException(
                String.format("Dentist %s %s already has %d appointments scheduled for the week of %s. Maximum 5 appointments per week allowed.",
                    appointment.getDentist().getFirstName(),
                    appointment.getDentist().getLastName(),
                    appointmentsInWeek,
                    weekStart.toLocalDate())
            );
        }
    }

    /**
     * Validates that a patient doesn't have outstanding unpaid bills
     * @param appointment The appointment being created/updated
     * @throws OutstandingBillException if the patient has unpaid bills
     */
    private void validatePatientOutstandingBills(Appointment appointment) throws OutstandingBillException {
        if (appointment.getPatient() == null) {
            return; // Cannot validate without patient
        }
        
        // Check if patient has outstanding bills
        boolean hasOutstandingBills = billService.hasOutstandingBills(appointment.getPatient().getPatientId());
        
        if (hasOutstandingBills) {
            throw new OutstandingBillException(
                String.format("Patient %s %s has outstanding unpaid bills. Please settle all outstanding bills before scheduling a new appointment.",
                    appointment.getPatient().getFirstName(),
                    appointment.getPatient().getLastName())
            );
        }
    }
}
