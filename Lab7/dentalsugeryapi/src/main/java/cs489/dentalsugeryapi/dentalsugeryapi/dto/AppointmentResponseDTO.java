package cs489.dentalsugeryapi.dentalsugeryapi.dto;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
    Integer appointmentId,
    String appointmentType,
    String appointmentStatus,
    LocalDateTime appointmentDateTime,
    PatientResponseDTO patientResponseDTO,
    DentistResponseDTO dentistResponseDTO,
    SurgeryLocationResponseDTO surgeryLocationResponseDTO
) {
}