package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record DentistResponseDTO(
    Integer dentistId,
    String firstName,
    String lastName,
    String contactNumber,
    String email,
    String specialization
) {
}