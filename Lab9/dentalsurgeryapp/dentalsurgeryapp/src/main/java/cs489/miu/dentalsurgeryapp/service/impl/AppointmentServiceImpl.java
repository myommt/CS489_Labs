package cs489.miu.dentalsurgeryapp.service.impl;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import cs489.miu.dentalsurgeryapp.dto.AppointmentRequestDTO;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.AppointmentStatus;
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

    // New methods for patient portal
    @Override
    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id.intValue()).map(appointment -> appointment);
    }

    @Override
    public Page<Appointment> findAppointmentsByPatient(Patient patient, Pageable pageable) {
        return appointmentRepository.findByPatient(patient, pageable);
    }

    @Override
    public Page<Appointment> findAppointmentsByPatientAndStatus(Patient patient, AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByPatientAndAppointmentStatus(patient, status.name(), pageable);
    }

    @Override
    public long countAppointmentsByPatient(Patient patient) {
        return appointmentRepository.countByPatient(patient);
    }

    @Override
    public long countUpcomingAppointmentsByPatient(Patient patient) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.countByPatientAndAppointmentDateTimeAfter(patient, now);
    }

    @Override
    public long countCompletedAppointmentsByPatient(Patient patient) {
        return appointmentRepository.countByPatientAndAppointmentStatus(patient, AppointmentStatus.COMPLETED.name());
    }

    @Override
    public Appointment createAppointment(AppointmentRequestDTO appointmentDto) throws AppointmentLimitExceededException, OutstandingBillException {
        try {
            // Validate required fields first
            if (appointmentDto.getPatientId() == null) {
                throw new IllegalArgumentException("Patient ID is required");
            }
            if (appointmentDto.getDentistId() == null) {
                throw new IllegalArgumentException("Dentist ID is required");
            }
            if (appointmentDto.getSurgeryLocationId() == null) {
                throw new IllegalArgumentException("Surgery location ID is required");
            }
            if (appointmentDto.getAppointmentDate() == null) {
                throw new IllegalArgumentException("Appointment date is required");
            }
            if (appointmentDto.getAppointmentTime() == null || appointmentDto.getAppointmentTime().trim().isEmpty()) {
                throw new IllegalArgumentException("Appointment time is required");
            }
            
            // Get the patient
            Patient patient = patientService.getPatientById(appointmentDto.getPatientId().intValue());
            if (patient == null) {
                throw new IllegalArgumentException("Patient not found with ID: " + appointmentDto.getPatientId());
            }
            
            // Get the dentist
            Optional<Dentist> dentistOpt = dentistService.findDentistById(appointmentDto.getDentistId().intValue());
            if (dentistOpt.isEmpty()) {
                throw new IllegalArgumentException("Dentist not found with ID: " + appointmentDto.getDentistId());
            }
            
            // Get the surgery location
            Optional<SurgeryLocation> surgeryLocationOpt = surgeryLocationService.findSurgeryLocationById(appointmentDto.getSurgeryLocationId().intValue());
            if (surgeryLocationOpt.isEmpty()) {
                throw new IllegalArgumentException("Surgery location not found with ID: " + appointmentDto.getSurgeryLocationId());
            }
            
            // Create new appointment
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setDentist(dentistOpt.get());
            appointment.setSurgeryLocation(surgeryLocationOpt.get());
            appointment.setAppointmentType(appointmentDto.getAppointmentType());
            
            // Handle appointment status safely
            String status = appointmentDto.getStatus() != null ? 
                appointmentDto.getStatus().name() : AppointmentStatus.PENDING.name();
            appointment.setAppointmentStatus(status);
            
            // Combine date and time with error handling
            try {
                LocalDateTime appointmentDateTime = LocalDateTime.of(
                    appointmentDto.getAppointmentDate(),
                    LocalTime.parse(appointmentDto.getAppointmentTime())
                );
                appointment.setAppointmentDateTime(appointmentDateTime);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date or time format. Date: " + 
                    appointmentDto.getAppointmentDate() + ", Time: " + appointmentDto.getAppointmentTime(), e);
            }
            
            // Note: Additional fields like reason would need to be added to Appointment model
            
            return addNewAppointment(appointment);
            
        } catch (AppointmentLimitExceededException | OutstandingBillException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error creating appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public Appointment updateAppointment(AppointmentRequestDTO appointmentDto) throws AppointmentLimitExceededException, OutstandingBillException {
        try {
            // Get existing appointment
            Optional<Appointment> existingOpt = findAppointmentById(appointmentDto.getAppointmentId());
            if (existingOpt.isEmpty()) {
                throw new IllegalArgumentException("Appointment not found with ID: " + appointmentDto.getAppointmentId());
            }
            
            Appointment existing = existingOpt.get();
            
            // Update fields
            if (appointmentDto.getDentistId() != null) {
                Optional<Dentist> dentistOpt = dentistService.findDentistById(appointmentDto.getDentistId().intValue());
                if (dentistOpt.isPresent()) {
                    existing.setDentist(dentistOpt.get());
                }
            }
            
            if (appointmentDto.getSurgeryLocationId() != null) {
                Optional<SurgeryLocation> surgeryLocationOpt = surgeryLocationService.findSurgeryLocationById(appointmentDto.getSurgeryLocationId().intValue());
                if (surgeryLocationOpt.isPresent()) {
                    existing.setSurgeryLocation(surgeryLocationOpt.get());
                }
            }
            
            if (appointmentDto.getAppointmentDate() != null && appointmentDto.getAppointmentTime() != null) {
                LocalDateTime appointmentDateTime = LocalDateTime.of(
                    appointmentDto.getAppointmentDate(),
                    LocalTime.parse(appointmentDto.getAppointmentTime())
                );
                existing.setAppointmentDateTime(appointmentDateTime);
            }
            
            if (appointmentDto.getStatus() != null) {
                existing.setAppointmentStatus(appointmentDto.getStatus().name());
            }
            
            return updateAppointment(existing);
            
        } catch (AppointmentLimitExceededException | OutstandingBillException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error updating appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // Methods for dentist portal
    @Override
    public Page<Appointment> findAppointmentsByDentist(Dentist dentist, Pageable pageable) {
        return appointmentRepository.findByDentist(dentist, pageable);
    }

    @Override
    public Page<Appointment> findAppointmentsByDentistAndStatus(Dentist dentist, AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByDentistAndAppointmentStatus(dentist, status.name(), pageable);
    }

    @Override
    public long countAppointmentsByDentist(Dentist dentist) {
        return appointmentRepository.countByDentist(dentist);
    }

    @Override
    public long countUpcomingAppointmentsByDentist(Dentist dentist) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.countByDentistAndAppointmentDateTimeAfter(dentist, now);
    }

    @Override
    public long countCompletedAppointmentsByDentist(Dentist dentist) {
        return appointmentRepository.countByDentistAndAppointmentStatus(dentist, AppointmentStatus.COMPLETED.name());
    }
}
