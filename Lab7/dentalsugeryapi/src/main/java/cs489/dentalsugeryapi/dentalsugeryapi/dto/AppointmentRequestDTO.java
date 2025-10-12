package cs489.dentalsugeryapi.dentalsugeryapi.dto;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
    String appointmentType,
    String appointmentStatus,
    LocalDateTime appointmentDateTime,
    PatientRequestDTO patientRequestDTO,
    DentistRequestDTO dentistRequestDTO,
    SurgeryLocationRequestDTO surgeryLocationRequestDTO
) {

}